package com.eydiz.studio.controller;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.eydiz.common.Constant;
import com.eydiz.common.MyUtil;
import com.eydiz.common.Pager;
import com.eydiz.member.MemberConstant;
import com.eydiz.studio.BrandSessionInfo;
import com.eydiz.studio.Project;
import com.eydiz.studio.ProjectCategory;
import com.eydiz.studio.ProjectHashtag;
import com.eydiz.studio.ProjectImage;
import com.eydiz.studio.ProjectNews;
import com.eydiz.studio.Reward;
import com.eydiz.studio.Send;
import com.eydiz.studio.StudioConstant;
import com.eydiz.studio.StudioService;

@Controller("studio.controller.studioProjectController")
@RequestMapping("/studio/project/*")
public class StudioProjectController implements Constant, StudioConstant, MemberConstant {

	private Logger logger = LoggerFactory.getLogger(StudioProjectController.class);
	private final static int ROWS = 9;

	@Autowired
	StudioService service;

	@Autowired
	Pager pager;

	private String getRealURI(String requestURI, String contextPath) {
		return requestURI.substring(contextPath.length() + REQUEST_MAPPING.length());
	}

	private void addModelURIAttribute(Model model, HttpServletRequest req, Integer projectNo) {
		StringBuilder uri = new StringBuilder(req.getRequestURI());
		if (req.getQueryString() != null && req.getQueryString().length() > 0) {
			uri.append("?" + req.getQueryString());
		}
		model.addAttribute(MemberConstant.ATTRIBUTE_URI, getRealURI(uri.toString(), req.getContextPath()));
		model.addAttribute(ATTRIBUTE_PROJECTNO, projectNo);
	}

	private String getRealPath(HttpSession session) {
		// "/"문자열 다음부터 추출해야...
		String cpRealPath = session.getServletContext().getRealPath("/");
		String upload = File.separator + FOLDER_UPLOADS_ROOT + File.separator + FOLDER_UPLOADS_PROJECT;
		return cpRealPath + upload;
	}

	////////////////////////////////////////////// 프로젝트
	@RequestMapping(value = { "/list" })
	public String list(Model model, HttpServletRequest req) {
		addModelURIAttribute(model, req, null);
		return VIEW_PROJECT_LIST;
	}

	@RequestMapping(value = { "/ajax/list", "/ajax/list/{page}" })
	@ResponseBody
	public Map<String, Object> ajaxList(@PathVariable(required = false) Integer page, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		try {
			if (page == null) {
				page = 1;
			}
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			int brandNo = bInfo.getBrandNo();
			int listProjectCount = service.listProjectCount(brandNo);
			int pageCount = pager.pageCount(ROWS, listProjectCount);
			int offset = pager.getOffset(page, ROWS);
			// 페이징 정보 계산과 해당하는 페이지의 프로젝트 불러오기
			Map<String, Object> param = new HashMap<>();
			param.put(ATTRIBUTE_ROWS, ROWS);
			param.put(ATTRIBUTE_OFFSET, offset);
			param.put(ATTRIBUTE_BRANDNO, brandNo);
			List<Project> listProject = service.listProject(param);
			map.put(JSON_RESULT, JSON_RESULT_OK);
			// 페이징 정보 입력
			map.put(ATTRIBUTE_CURRENT_PAGE, page);
			map.put(ATTRIBUTE_PAGE_COUNT, pageCount);
			map.put(ATTRIBUTE_PROJECT, listProject);
		} catch (Exception e) {
			e.printStackTrace();
			map.put(JSON_RESULT, JSON_RESULT_ERROR);
			map.put(JSON_RESULT_ERROR, e.getMessage());
		}
		return map;
	}

	// 프로젝트 대시보드
	@RequestMapping(value = "/{projectNo}/dashboard", method = RequestMethod.GET)
	public String dashboard(@PathVariable Integer projectNo, Model model, HttpSession session) {
		try {
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			Project project = service.readProject(projectNo, bInfo.getBrandNo());
			model.addAttribute(ATTRIBUTE_PROJECT, project);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return VIEW_PROJECT_DASHBOARD;
	}

	// 프로젝트 조회/편집
	@RequestMapping(value = { "/register", "/{projectNo}/register" }, method = RequestMethod.GET)
	public String registerForm(@PathVariable(required = false) Integer projectNo, Model model, HttpServletRequest req,
			HttpSession session) {
		try {
			BrandSessionInfo info = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			if (projectNo == null) {
				// 새로 프로젝트 만들기
				Project project = new Project();
				project.setBrandNo(info.getBrandNo());
				projectNo = service.insertEmptyProject(project);
				return "redirect:" + String.format(API_PROJECT_REGISTER, projectNo);
			}
			////////////// 프로젝트 정보 불러오기
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			Project project = service.readProject(projectNo, bInfo.getBrandNo());
			if (project == null) {
				// 자신의 브랜드의 프로젝트가 아니면 null을 반환함
				return String.format(API_PROJECT_DASHBOARD, projectNo);
			}
			List<ProjectCategory> category = service.listCategory();
			model.addAttribute(ATTRIBUTE_CATEGORY, category);
			model.addAttribute(ATTRIBUTE_PROJECT, project);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		addModelURIAttribute(model, req, projectNo);
		return VIEW_PROJECT_REGISTER;
	}

	// 프로젝트 저장하기
	@RequestMapping(value = "/{projectNo}/register", method = RequestMethod.POST)
	public String updateProject(@PathVariable Integer projectNo, Project project, HttpSession session) {
		try {
			if (projectNo == null) {
				throw new Exception("프로젝트 번호가 전달되지 않아 프로젝트를 저장할 수 없음");
			}
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			project.setProjectNo(projectNo);
			project.setBrandNo(bInfo.getBrandNo());
			if (project.getProjectGoalAmount() == 0) {
				project.setProjectGoalAmount(1000000); // 기본 100만 원으로 설정
			}
			service.updateProjectBasic(project);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return "redirect:" + String.format(API_PROJECT_REGISTER, projectNo);
	}

	// 이미지 업로드
	@RequestMapping(value = "/{projectNo}/register/upload/image", method = RequestMethod.POST)
	@ResponseBody
	public String uploadImage(@PathVariable Integer projectNo, ProjectImage uploadImage, HttpSession session) {
		JSONObject json = new JSONObject();
		try {
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			Project project = service.readProject(projectNo, bInfo.getBrandNo());
			String realPath = getRealPath(session);
			String cp = session.getServletContext().getContextPath();
			String uriPath = cp + "/" + FOLDER_UPLOADS_ROOT + "/" + FOLDER_UPLOADS_PROJECT;
			String saveFilename = service.uploadProjectImage(project, uploadImage.getUploadImage(), realPath, uriPath);
			if (saveFilename != null) {
				json.put(JSON_RESULT, JSON_RESULT_OK);
				json.put(JSON_IMAGE_URL, saveFilename);
			} else {
				throw new Exception("이미지 업로드 실패");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			json.put(JSON_RESULT, JSON_RESULT_ERROR);
			json.put(JSON_RESULT_ERROR_MESSAGE, e.getMessage());
		}
		return json.toString();
	}

	// 이미지 삭제
	@RequestMapping(value = "/{projectNo}/register/delete/image", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> deleteImage(@PathVariable Integer projectNo, HttpSession session) {
		Map<String, String> map = new HashMap<>();
		try {
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			Project project = service.readProject(projectNo, bInfo.getBrandNo());
			service.deleteProjectImage(project, getRealPath(session));
			map.put(JSON_RESULT, JSON_RESULT_OK);
		} catch (Exception e) {
			map.put(JSON_RESULT, JSON_RESULT_ERROR);
			map.put(JSON_RESULT_ERROR_MESSAGE, e.getMessage());
			e.printStackTrace();
		}
		return map;
	}

	/// 해시태그
	@RequestMapping(value = "/{projectNo}/hashtag/insert/{keyword}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> insertHashtag(@PathVariable Integer projectNo, @PathVariable String keyword) {
		Map<String, Object> result = new HashMap<>();
		try {
			if (keyword == null) {
				throw new Exception("태그가 입력되지 않았습니다.");
			}
			ProjectHashtag hashtag = new ProjectHashtag(projectNo, keyword);
			service.insertHashtag(hashtag);
			result.put(JSON_RESULT, JSON_RESULT_OK);
			result.put(JSON_HASHTAG, hashtag);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(JSON_RESULT, JSON_RESULT_ERROR);
			result.put(JSON_RESULT_ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/{projectNo}/hashtag/view", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> readHashtag(@PathVariable Integer projectNo) {
		Map<String, Object> result = new HashMap<>();
		try {
			List<ProjectHashtag> list = service.readHashtag(projectNo);
			result.put(JSON_RESULT, JSON_RESULT_OK);
			result.put(JSON_HASHTAG, list);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(JSON_RESULT, JSON_RESULT_ERROR);
			result.put(JSON_RESULT_ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/{projectNo}/hashtag/delete/{keyword}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteHashtag(@PathVariable Integer projectNo, @PathVariable String keyword) {
		Map<String, Object> result = new HashMap<>();
		try {
			ProjectHashtag tag = new ProjectHashtag(projectNo, keyword);
			service.deleteHashtag(tag);
			result.put(JSON_RESULT, JSON_RESULT_OK);
			result.put(JSON_HASHTAG, tag);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(JSON_RESULT, JSON_RESULT_ERROR);
			result.put(JSON_RESULT_ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}

	/// 리워드 설계
	@RequestMapping(value = "/{projectNo}/reward")
	public String rewardForm(@PathVariable Integer projectNo, Model model, HttpServletRequest req,
			HttpSession session) {
		addModelURIAttribute(model, req, projectNo);
		BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
		Project project = service.readProject(projectNo, bInfo.getBrandNo());
		model.addAttribute(ATTRIBUTE_BRANDNO, bInfo.getBrandNo());
		model.addAttribute(ATTRIBUTE_PROJECT, project);
		return VIEW_PROJECT_REWARD;
	}

	// 리워드 설계
	@RequestMapping(value = "/{projectNo}/reward/add", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> rewardPost(@PathVariable Integer projectNo, Reward reward) {
		Map<String, Object> result = new HashMap<>();
		try {
			if (projectNo == null) {
				throw new Exception("projectNo is null");
			}
			reward.setProjectNo(projectNo);
			System.out.println(reward.toString());
			service.insertReward(reward);
			result.put(JSON_RESULT, JSON_RESULT_OK);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(JSON_RESULT, JSON_RESULT_ERROR);
			result.put(JSON_RESULT_ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}

	// 리워드 조회
	@RequestMapping(value = "/{projectNo}/reward/list", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> rewardList(@PathVariable Integer projectNo, HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		List<Reward> rewards = null;
		try {
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			rewards = service.selectReward(projectNo, bInfo.getBrandNo());
			result.put(JSON_RESULT, JSON_RESULT_OK);
			result.put(JSON_REWARD_LIST, rewards);
		} catch (Exception e) {
			e.printStackTrace();
			result.put(JSON_RESULT, JSON_RESULT_ERROR);
			result.put(JSON_RESULT_ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}

	// 리워드 수정
	@RequestMapping(value = "/{projectNo}/reward/{rewardNo}/update", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateReward(@PathVariable Integer projectNo, @PathVariable Integer rewardNo,
			Reward reward, HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		try {
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			reward.setProjectNo(projectNo);
			reward.setRewardNo(rewardNo);
			reward.setBrandNo(bInfo.getBrandNo());
			service.updateReward(reward);
			result.put(JSON_RESULT, JSON_RESULT_OK);
		} catch (Exception e) {
			result.put(JSON_RESULT, JSON_RESULT_ERROR);
			result.put(JSON_RESULT_ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}

	// 리워드 삭제
	@RequestMapping(value = "/{projectNo}/reward/{rewardNo}/delete", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteReward(@PathVariable Integer projectNo, @PathVariable Integer rewardNo,
			HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		try {
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			service.deleteReward(rewardNo, projectNo, bInfo.getBrandNo());
			result.put(JSON_RESULT, JSON_RESULT_OK);
		} catch (Exception e) {
			result.put(JSON_RESULT, JSON_RESULT_ERROR);
			result.put(JSON_RESULT_ERROR_MESSAGE, e.getMessage());
		}
		return result;
	}

	// 스토리
	// 프로젝트 조회/편집
	@RequestMapping(value = { "/{projectNo}/story" }, method = RequestMethod.GET)
	public String storyForm(@PathVariable Integer projectNo, Model model, HttpServletRequest req, HttpSession session) {
		try {
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			Project project = service.readProject(projectNo, bInfo.getBrandNo());
			model.addAttribute(ATTRIBUTE_PROJECT, project);
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		addModelURIAttribute(model, req, projectNo);
		return VIEW_PROJECT_STORY;
	}

	// 스토리 변경
	@RequestMapping(value = "/{projectNo}/story", method = RequestMethod.POST)
	public String storySubmit(@PathVariable Integer projectNo, Project project, Model model, HttpSession session) {
		try {
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			project.setProjectNo(projectNo);
			project.setBrandNo(bInfo.getBrandNo());
			service.updateStory(project);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:" + String.format(API_PROJECT_STORY, projectNo);
	}

	// 프로젝트 삭제
	@RequestMapping(value = "/{projectNo}/delete", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> deleteProject(@PathVariable Integer projectNo, HttpSession session) {
		Map<String, Object> map = new HashMap<>();
		try {
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			service.deleteProject(projectNo, bInfo.getBrandNo());
			map.put(JSON_RESULT, JSON_RESULT_OK);
		} catch (Exception e) {
			map.put(JSON_RESULT, JSON_RESULT_ERROR);
			map.put(JSON_RESULT_ERROR_MESSAGE, e.getMessage());
		}
		return map;
	}

	// 제출하기 버튼
	@RequestMapping(value = "/{projectNo}/submit")
	public String submitProject(@PathVariable Integer projectNo, HttpSession session) {
		try {
			BrandSessionInfo bInfo = (BrandSessionInfo) session.getAttribute(SESSION_BRAND);
			final int statusNo = 1; // 제출 완료 상태
			final String memo = "제출 완료";
			service.insertProjectStatusList(projectNo, bInfo.getBrandNo(), statusNo, memo);
			service.updateProjectStatus(projectNo, bInfo.getBrandNo(), statusNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:" + String.format(API_PROJECT_DASHBOARD, projectNo);
	}

	// 새소식 ----------------------------------------------

	@Autowired
	private MyUtil myUtil;

	@RequestMapping(value = "/{projectNo}/news/list")
	public String newsList(@PathVariable Integer projectNo,
			@RequestParam(value = "page", defaultValue = "1") int current_page,
			@RequestParam(defaultValue = "title") String condition, @RequestParam(defaultValue = "") String keyword,
			HttpServletRequest req, Model model) throws Exception {
		String cp = req.getContextPath();

		int rows = 10;
		int total_page = 0;
		int dataCount = 0;

		if (req.getMethod().equalsIgnoreCase("GET")) {
			keyword = URLDecoder.decode(keyword, "utf-8");
		}

		Map<String, Object> map = new HashMap<>();
		map.put("condition", condition);
		map.put("keyword", keyword);
		map.put("projectNo", projectNo);

		dataCount = service.dataCount(map);

		if (dataCount != 0) {
			total_page = myUtil.pageCount(rows, dataCount);
		}

		if (total_page < current_page) {
			current_page = total_page;
		}

		int offset = (current_page - 1) * rows;
		if (offset < 0)
			offset = 0;
		map.put("offset", offset);
		map.put("rows", rows);

		List<ProjectNews> list = service.listProjectNews(map);

		int listNum = 0;
		int num = 0;
		for (ProjectNews dto : list) {
			listNum = dataCount - (offset + num);
			dto.setListNum(listNum);
			num++;

		}

		String query = "";
		String listUrl = cp + "/studio/project/" + projectNo + "/news/list";
		String readUrl = cp + "/studio/project/" + projectNo + "/news/read?page=" + current_page;

		if (keyword.length() != 0) {
			listUrl += "?query=" + query;
			readUrl += "&" + query;
		}

		String paging = myUtil.paging(current_page, total_page, listUrl);
		BrandSessionInfo bInfo = (BrandSessionInfo) req.getSession().getAttribute(SESSION_BRAND);
		model.addAttribute("project", service.readProject(projectNo, bInfo.getBrandNo()));
		model.addAttribute("list", list);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("readUrl", readUrl);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("condition", condition);
		model.addAttribute("keyword", keyword);
		model.addAttribute("paging", paging);

		return ".studioLayout.newsList";
	}

	@RequestMapping(value = "/{projectNo}/news/write", method = RequestMethod.GET)
	public String newsWriteForm(@PathVariable Integer projectNo, Model model, HttpServletRequest req) throws Exception {
		BrandSessionInfo bInfo = (BrandSessionInfo) req.getSession().getAttribute(SESSION_BRAND);
		model.addAttribute("project", service.readProject(projectNo, bInfo.getBrandNo()));
		model.addAttribute("projectNo", projectNo);
		model.addAttribute("mode", "write");
		return ".studioLayout.newsWrite";
	}

	@RequestMapping(value = "/{projectNo}/news/write", method = RequestMethod.POST)
	public String newsWriteSubmit(@PathVariable Integer projectNo, ProjectNews dto, HttpSession session)
			throws Exception {
		try {
			dto.setProjectNo(projectNo);
			service.insertProjectNews(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/studio/project/" + projectNo + "/news/list";
	}

	@RequestMapping(value = "/{projectNo}/news/read")
	public String newsRead(@PathVariable Integer projectNo, @RequestParam String page, Model map,
			@RequestParam(defaultValue = "title") String condition, @RequestParam(defaultValue = "") String keyword,
			@RequestParam int newsNo) throws Exception {
		keyword = URLDecoder.decode(keyword, "utf-8");

		String query = "page=" + page;
		if (keyword.length() != 0) {
			query = "condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
		}

		ProjectNews dto = service.readProjectNews(newsNo);

		if (dto == null) {
			return "redirect:/studio/project/" + projectNo + "/news/list?" + query;
		}

		map.addAttribute("keyword", keyword);
		map.addAttribute("condition", condition);
		map.addAttribute("query", query);
		map.addAttribute("dto", dto);
		map.addAttribute("newsNo", newsNo);
		map.addAttribute("projectNo", projectNo);
		map.addAttribute("page", page);

		return ".studioLayout.newsRead";
	}

	@RequestMapping(value = "/{projectNo}/news/update", method = RequestMethod.GET)
	public String newsUpdateForm(@PathVariable Integer projectNo, Model model, @RequestParam String page,
			@RequestParam int newsNo) throws Exception {
		ProjectNews dto = service.readProjectNews(newsNo);

		if (dto == null) {
			return "redirect:/studio/project/" + projectNo + "/news/list?page=" + page;
		}

		model.addAttribute("dto", dto);
		model.addAttribute("page", page);
		model.addAttribute("projectNo", projectNo);
		model.addAttribute("mode", "update");
		return ".studioLayout.newsWrite";
	}

	@RequestMapping(value = "/{projectNo}/news/update", method = RequestMethod.POST)
	public String newsUpdateSubmit(@PathVariable Integer projectNo, ProjectNews dto, String page) throws Exception {
		try {
			dto.setProjectNo(projectNo);
			service.updateProjectNews(dto);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/studio/project/" + projectNo + "/news/list?page=" + page;
	}

	@RequestMapping(value = "/{projectNo}/news/delete")
	public String delete(@PathVariable Integer projectNo, @RequestParam int newsNo,
			@RequestParam(required = false, defaultValue = "1") String page,
			@RequestParam(defaultValue = "title") String condition, @RequestParam(defaultValue = "") String keyword)
			throws Exception {
		keyword = URLDecoder.decode(keyword, "utf-8");

		String query = "page=" + page;
		if (keyword.length() != 0) {
			query = "condition=" + condition + "&keyword=" + URLEncoder.encode(keyword, "utf-8");
		}

		service.deleteProjectNews(newsNo);

		return "redirect:/studio/project/" + projectNo + "/news/list?" + query;
	}

	// 펀딩/후원 현황 ------------------------------------------
	@RequestMapping(value = "/shipping/manage/{projectNo}")
	public String listSendmanage(@PathVariable Integer projectNo, @RequestParam(defaultValue = "1") int current_page,
			@RequestParam(defaultValue = "buyNo") String condition, @RequestParam(defaultValue = "") String keyword,
			HttpServletRequest req, Model model) throws Exception {
		String cp = req.getContextPath();

		int rows = 10;
		int total_page = 0;
		int dataCount = 0;

		if (req.getMethod().equalsIgnoreCase("GET")) {
			keyword = URLDecoder.decode(keyword, "utf-8");
		}

		Map<String, Object> map = new HashMap<>();
		map.put("condition", condition);
		map.put("keyword", keyword);
		map.put("projectNo", projectNo);

		dataCount = service.manageDataCount(map);

		if (dataCount != 0) {
			total_page = myUtil.pageCount(rows, dataCount);
		}

		if (total_page < current_page) {
			current_page = total_page;
		}

		int offset = (current_page - 1) * rows;
		if (offset < 0)
			offset = 0;
		map.put("offset", offset);
		map.put("rows", rows);

		List<Send> list = service.listSendmanage(map);

		int listNum = 0;
		int num = 0;
		for (Send dto : list) {
			listNum = dataCount - (offset + num);
			dto.setListNum(listNum);
			num++;
		}

		String query = "";
		String listUrl = cp + "/studio/project/shipping/manage/{projectNo}";
		String readUrl = cp + "/studio/project/shipping/manage/{projectNo}?page=" + current_page;

		if (keyword.length() != 0) {
			listUrl += "?query=" + query;
			readUrl += "&" + query;
		}

		String paging = myUtil.paging(current_page, total_page, listUrl);
		model.addAttribute("managelist", list);
		model.addAttribute("managedataCount", dataCount);
		model.addAttribute("readUrl", readUrl);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("condition", condition);
		model.addAttribute("keyword", keyword);
		model.addAttribute("paging", paging);

		return ".studioLayout.managelist";
	}

	@RequestMapping(value = "/shipping/ready/{projectNo}")
	public String listSendready(@PathVariable Integer projectNo, @RequestParam(defaultValue = "1") int current_page,
			@RequestParam(defaultValue = "buyNo") String condition, @RequestParam(defaultValue = "") String keyword,
			HttpServletRequest req, Model model) throws Exception {
		String cp = req.getContextPath();

		int rows = 10;
		int total_page = 0;
		int dataCount = 0;

		if (req.getMethod().equalsIgnoreCase("GET")) {
			keyword = URLDecoder.decode(keyword, "utf-8");
		}

		Map<String, Object> map = new HashMap<>();
		map.put("condition", condition);
		map.put("keyword", keyword);
		map.put("projectNo", projectNo);

		dataCount = service.readyDataCount(map);

		if (dataCount != 0) {
			total_page = myUtil.pageCount(rows, dataCount);
		}

		if (total_page < current_page) {
			current_page = total_page;
		}

		int offset = (current_page - 1) * rows;
		if (offset < 0)
			offset = 0;
		map.put("offset", offset);
		map.put("rows", rows);

		List<Send> list = service.listSendready(map);

		int listNum = 0;
		int num = 0;
		for (Send dto : list) {
			listNum = dataCount - (offset + num);
			dto.setListNum(listNum);
			num++;
		}

		String query = "";
		String listUrl = cp + "/studio/project/shipping/ready/{projectNo}";
		String readUrl = cp + "/studio/project/shipping/ready/{projectNo}?page=" + current_page;

		if (keyword.length() != 0) {
			listUrl += "?query=" + query;
			readUrl += "&" + query;
		}

		String paging = myUtil.paging(current_page, total_page, listUrl);
		model.addAttribute("readylist", list);
		model.addAttribute("readyCount", dataCount);
		model.addAttribute("readUrl", readUrl);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("condition", condition);
		model.addAttribute("keyword", keyword);
		model.addAttribute("paging", paging);

		return ".studioLayout.sendreadylist";
	}

	// 펀딩/후원 현황 ------------------------------------------
	@RequestMapping(value = "/{projectNo}/view/list")
	public String viewList(@PathVariable Integer projectNo, HttpServletRequest req,
			@RequestParam(value = "page", defaultValue = "1") int current_page, Model model) throws Exception {
		int rows = 10;
		int total_page = 0;
		int fundingViewDataCount = 0;

		String cp = req.getContextPath();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("projectNo", projectNo);
		fundingViewDataCount = service.fundingViewDataCount(map);

		if (fundingViewDataCount != 0) {
			total_page = myUtil.pageCount(rows, fundingViewDataCount);
		}

		if (total_page < current_page) {
			current_page = total_page;
		}

		int offset = (current_page - 1) * rows;
		if (offset < 0)
			offset = 0;
		map.put("offset", offset);
		map.put("rows", rows);

		List<Project> list = service.listFundingView(map);

		int listNum = 0;
		int num = 0;
		for (Project dto : list) {
			listNum = fundingViewDataCount - (offset + num);
			dto.setListNum(listNum);
			num++;
		}

		String listUrl = cp + "/studio/project/" + projectNo + "/view/list";
		String readUrl = cp + "/studio/project/" + projectNo + "/view/read?page=" + current_page;

		String paging = myUtil.paging(current_page, total_page, listUrl);
		BrandSessionInfo bInfo = (BrandSessionInfo) req.getSession().getAttribute(SESSION_BRAND);
		model.addAttribute("project", service.readProject(projectNo, bInfo.getBrandNo()));
		model.addAttribute("list", list);
		model.addAttribute("fundingViewDataCount", fundingViewDataCount);
		model.addAttribute("readUrl", readUrl);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("paging", paging);

		return ".studioLayout.viewList";
	}

	@RequestMapping(value = "/{projectNo}/view/read")
	public String readFundingView(@PathVariable Integer projectNo, @RequestParam String page, Model map,
			HttpServletRequest req, @RequestParam int buyNo) throws Exception {

		List<Reward> list = service.readFundingView(buyNo);

		if (list == null || list.size() == 0) {
			return "redirect:/studio/project/" + projectNo + "/view/list?page=" + page;
		}
		BrandSessionInfo bInfo = (BrandSessionInfo) req.getSession().getAttribute(SESSION_BRAND);
		map.addAttribute("project", service.readProject(projectNo, bInfo.getBrandNo()));
		map.addAttribute("rewards", list);
		map.addAttribute("buyNo", buyNo);
		map.addAttribute("projectNo", projectNo);
		map.addAttribute("page", page);

		return ".studioLayout.viewRead";
	}

	// 발송 관리
	@RequestMapping(value = "/shipping/send/{projectNo}")
	public String sendList(@PathVariable Integer projectNo, @RequestParam(defaultValue = "1") int current_page,
			@RequestParam(defaultValue = "buyNo") String condition, @RequestParam(defaultValue = "") String keyword,
			HttpServletRequest req, Model model) throws Exception {
		String cp = req.getContextPath();
	
		int rows = 10;
		int total_page = 0;
		int dataCount = 0;
	
		if (req.getMethod().equalsIgnoreCase("GET")) {
			keyword = URLDecoder.decode(keyword, "utf-8");
		}
	
		Map<String, Object> map = new HashMap<>();
		map.put("condition", condition);
		map.put("keyword", keyword);
		map.put("projectNo", projectNo);
	
		dataCount = service.sendDataCount(map);
	
		if (dataCount != 0) {
			total_page = myUtil.pageCount(rows, dataCount);
		}
	
		if (total_page < current_page) {
			current_page = total_page;
		}
	
		int offset = (current_page - 1) * rows;
		if (offset < 0)
			offset = 0;
		map.put("offset", offset);
		map.put("rows", rows);
	
		List<Send> list = service.listSend(map);
	
		int listNum = 0;
		int num = 0;
		for (Send dto : list) {
			listNum = dataCount - (offset + num);
			dto.setListNum(listNum);
			num++;
		}
	
		String query = "";
		String listUrl = cp + "/studio/project/shipping/send/{projectNo}";
		String readUrl = cp + "/studio/project/shipping/send/{projectNo}?page=" + current_page;
	
		if (keyword.length() != 0) {
			listUrl += "?query=" + query;
			readUrl += "&" + query;
		}
	
		String paging = myUtil.paging(current_page, total_page, listUrl);
		model.addAttribute("sendlist", list);
		model.addAttribute("dataCount", dataCount);
		model.addAttribute("readUrl", readUrl);
		model.addAttribute("page", current_page);
		model.addAttribute("total_page", total_page);
		model.addAttribute("condition", condition);
		model.addAttribute("keyword", keyword);
		model.addAttribute("paging", paging);
	
		return ".studioLayout.sendlist";
	}

	@RequestMapping(value = "/shipping/article/{projectNo}")
	public String sendArticle(@PathVariable Integer projectNo,
			@RequestParam(required = false, defaultValue = "0") int buyNo, @RequestParam String page,
			@RequestParam(defaultValue = "buyNo") String condition, @RequestParam(defaultValue = "") String keyword,
			HttpSession session, Model model) throws Exception {
		keyword = URLDecoder.decode(keyword, "utf-8");

		String query = "page=" + page;
		if (keyword.length() != 0) {
			query = "&condition=" + condition + "&keyword=" + keyword + URLEncoder.encode(keyword, "utf-8");
		}

		Send dto = service.readSend(projectNo, buyNo);

		if (dto == null) {
			return "redirect:/studio/project/shipping/send/{projectNo}?" + query;
		}

		Map<String, Object> map = new HashMap<>();
		map.put("condition", condition);
		map.put("keyword", keyword);

		model.addAttribute("dto", dto);
		model.addAttribute("page", page);
		model.addAttribute("query", query);

		return ".studioLayout.sendContent";
	}

	@RequestMapping(value="updateState")
	public String updateStatusNo (
			@RequestParam int buyNo,
			@RequestParam int projectNo,
			@RequestParam String page
			) throws Exception {

		try {
			service.updateStatus(buyNo);
		} catch (Exception e) {
		}
		
		return "redirect:/studio/project/shipping/ready/"+projectNo+"?page="+page;
	}

}
