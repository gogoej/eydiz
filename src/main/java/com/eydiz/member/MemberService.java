package com.eydiz.member;

import java.util.List;
import java.util.Map;

public interface MemberService {
	public Member loginMember(Member dto) throws AuthenticationException;
	
	public boolean isTakenId(String memberId);
	public boolean isTakenEmail(String memberEmail);


	public boolean isValidateNewMember(Member dto) throws JoinException;
	public void insertMember(Member dto) throws JoinException, Exception;
	
	public void updateMembership(Map<String, Object> map) throws Exception;
	public void updateMember(Member dto) throws Exception;
	
	public Member readMember(String userId);
	
	public void deleteMember(Map<String, Object> map) throws Exception;
	
	public int dataCount(Map<String, Object> map);
	public List<Member> listMember(Map<String, Object> map);
	
	public void updateMemberImageUrl(Member member) throws Exception;
}
