package com.co.kr.controller;


	import com.co.kr.vo.Book;
	import org.springframework.stereotype.Controller;
	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.servlet.ModelAndView;

	@Controller
	@RequestMapping("thymeleaf")
	public class BookInfoController {
	    
	    @RequestMapping("selectBookInfo")
	    ModelAndView selectBooktInfo() {
	        ModelAndView mav = new ModelAndView("/selectBookInfo");
	        
	        Book book = new Book();
	        book.setId("210000001");
	        book.setName("Anne Marie");
	        book.setAge(29);
	        
	        /** thymeleaf에서 사용할 object명, object를 ModelAndview에 넣어준다. */
			mav.setViewName("book/bookList.html");
	        mav.addObject("book", book);
	        
	        
	        return mav;
	    }
	}

