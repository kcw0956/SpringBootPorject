package com.co.kr.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.BoardListDomain2;
import com.co.kr.domain.LoginDomain;
import com.co.kr.service.UploadService;
import com.co.kr.service.UploadService2;
import com.co.kr.service.UserService;
import com.co.kr.service.UserService2;
import com.co.kr.util.CommonUtils;
import com.co.kr.util.Pagination;
import com.co.kr.vo.LoginVO;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j 
@RequestMapping(value = "/")
public class UserController2 {
	
	@Autowired
	private UserService2 userService;
	
	@Autowired
	private UploadService2 uploadService;


  // 좌측 메뉴 클릭시 보드화면 이동 (로그인된 상태)
	@RequestMapping(value = "bdList")
	public ModelAndView bdList() { 
		ModelAndView mav = new ModelAndView();
		List<BoardListDomain2> items = uploadService.boardList2();
		System.out.println("items ==> "+ items);
		mav.addObject("items", items);
		mav.setViewName("book/bookList.html");
		return mav; 
	};
	
	
	
	//대시보드 리스트 보여주기
	@GetMapping("mbList")
	public ModelAndView mbList(HttpServletRequest request) {
			
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		
		//session저장 page 가져오기
		String page = (String) session.getAttribute("page"); // session에 담고 있는 page 꺼냄
		//request param 저장 page 가져오기
		String paramPage = request.getParameter("page");
		
		
		if(paramPage != null) { //param 있으면
			session.setAttribute("page", paramPage);
		}else if(page != null) { //session 있으면
			session.setAttribute("page", page);
		}else {
			session.setAttribute("page", "1");
		}
		
		//페이지네이션
		mav = mbListCall(request);  //리스트만 가져오기
		
		mav.setViewName("admin/adminList.html");
		return mav; 
	};
	
	
	//페이징으로 리스트 가져오기 
    public ModelAndView mbListCall(HttpServletRequest request) { //클릭페이지 널이면 
		ModelAndView mav = new ModelAndView();
		//페이지네이션 쿼리 참고
    // SELECT * FROM jsp.member order by mb_update_at limit 1, 5; {offset}{limit}

		//전체 갯수
		int totalcount = userService.mbGetAll();
		int contentnum = 10; // 데이터 가져올 갯수 
		
		
		//데이터 유무 분기때 사용
		boolean itemsNotEmpty;
		
		if(totalcount > 0) { // 데이터 있을때
			
			// itemsNotEmpty true일때만, 리스트 & 페이징 보여주기
			itemsNotEmpty = true;
			//페이지 표현 데이터 가져오기
			Map<String,Object> pagination = Pagination.pagination(totalcount, request);
			
			Map map = new HashMap<String, Integer>();
	        map.put("offset",pagination.get("offset"));
	        map.put("contentnum",contentnum);
			
	        //페이지별 데이터 가져오기
			List<LoginDomain> loginDomain = userService.mbAllList(map);
			
			//모델객체 넣어주기
			mav.addObject("itemsNotEmpty", itemsNotEmpty);
			mav.addObject("items", loginDomain);
			mav.addObject("rowNUM", pagination.get("rowNUM"));
			mav.addObject("pageNum", pagination.get("pageNum"));
			mav.addObject("startpage", pagination.get("startpage"));
			mav.addObject("endpage", pagination.get("endpage"));
			
		}else {
			itemsNotEmpty = false;
		}
		
		return mav;
	};
	
	
	//수정페이지 이동
	@GetMapping("/modify/{mbSeq}")
    public ModelAndView mbModify(@PathVariable("mbSeq") String mbSeq, RedirectAttributes re) throws IOException {
		ModelAndView mav = new ModelAndView();
		re.addAttribute("mbSeq", mbSeq);
		mav.setViewName("redirect:/mbEditList");
		return mav;
	};
	
	
	
	
	//수정업데이트
	@RequestMapping("/update")
	public ModelAndView mbModify(LoginVO loginVO, HttpServletRequest request, RedirectAttributes re) throws IOException {
		
		ModelAndView mav = new ModelAndView();
		
		//page 초기화
		HttpSession session = request.getSession();
		
		String page = "1"; // 업데이트 되면 가장 첫화면으로 갈 것이다.  
		
		//db 업데이트
		LoginDomain loginDomain = null; //초기화
		String IP = CommonUtils.getClientIP(request);
		loginDomain = LoginDomain.builder()
				.mbSeq(Integer.parseInt(loginVO.getSeq()))
				.mbId(loginVO.getId())
				.mbPw(loginVO.getPw())
				.mbLevel(loginVO.getLevel())
				.mbIp(IP)
				.mbUse("Y")
				.build();
		userService.mbUpdate(loginDomain);
		
		//첫 페이지로 이동
		re.addAttribute("page",page); // 리다이렉트시 파람으로 실어서 보냄
		mav.setViewName("redirect:/mbList");
		return mav;
	};
	
	
	//삭제
	@GetMapping("/remove/{mbSeq}")
    public ModelAndView mbRemove(@PathVariable("mbSeq") String mbSeq, RedirectAttributes re, HttpServletRequest request) throws IOException {
		ModelAndView mav = new ModelAndView();
		
		//db 삭제
		Map map = new HashMap<String, String>();
		map.put("mbSeq", mbSeq);
		userService.mbRemove(map);
		//page 초기화
		HttpSession session = request.getSession();
				
		//보고 있던 현재 페이지로 이동
		re.addAttribute("page",session.getAttribute("page")); // 리다이렉트시 파람으로 실어서 보냄
		mav.setViewName("redirect:/mbList");
		return mav;
	};
	
	
	
	@RequestMapping(value = "bookList")
	public ModelAndView bookList() { 
		ModelAndView mav = new ModelAndView();
		List<BoardListDomain2> items = uploadService.boardList2();
		System.out.println("items ==> "+ items);
		mav.addObject("items", items);
		mav.setViewName("book/bookList.html");
		return mav; 
	};
}