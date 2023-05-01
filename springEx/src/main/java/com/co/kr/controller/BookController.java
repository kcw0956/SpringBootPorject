package com.co.kr.controller;


import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.code.Code;
import com.co.kr.domain.BookFileDomain;
import com.co.kr.domain.BookListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.service.BookUploadService;
import com.co.kr.vo.BookListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BookController {
	
	@Autowired
	private BookUploadService bookuploadService;

	
	@PostMapping(value = "bkupload")
	public ModelAndView bkUpload(BookListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException, ParseException  {
		
		ModelAndView mav = new ModelAndView();
		int bdSeq = bookuploadService.bookfileProcess(fileListVO, request, httpReq);
		fileListVO.setContent(""); //초기화
		fileListVO.setTitle(""); //초기화
		
		// 화면에서 넘어올때는 bdSeq String이라 string으로 변환해서 넣어즘
		mav = bkSelectOneCall(fileListVO, String.valueOf(bdSeq),request);
		mav.setViewName("book/bookList.html");
		return mav;
		
	}
	
	//리스트 하나 가져오기 따로 함수뺌
		public ModelAndView bkSelectOneCall(@ModelAttribute("fileListVO") BookListVO fileListVO, String bdSeq, HttpServletRequest request) {
			ModelAndView mav = new ModelAndView();
			HashMap<String, Object> map = new HashMap<String, Object>();
			HttpSession bksession = request.getSession();
			
			map.put("bdSeq", Integer.parseInt(bdSeq));
			BookListDomain boardListDomain =bookuploadService.bookSelectOne(map);
			System.out.println("boardListDomain"+boardListDomain);
			List<BookFileDomain> fileList =  bookuploadService.bookSelectOneFile(map);
			
			for (BookFileDomain list : fileList) {
				String path = list.getUpFilePath().replaceAll("\\\\", "/");
				list.setUpFilePath(path);
			}
			mav.addObject("detail", boardListDomain);
			mav.addObject("files", fileList);

			//삭제시 사용할 용도
			bksession.setAttribute("files", fileList);
			return mav;
			}
			//detail
			@GetMapping("bkdetail")
		    public ModelAndView bkDetail(@ModelAttribute("fileListVO") BookListVO fileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
				ModelAndView mav = new ModelAndView();
				//하나파일 가져오기
				mav = bkSelectOneCall(fileListVO, bdSeq,request);
				mav.setViewName("book/bookList.html");
				return mav;
			
		}
			
			@GetMapping("bkedit")
			public ModelAndView bkedit(BookListVO fileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
				ModelAndView mav = new ModelAndView();

				HashMap<String, Object> map = new HashMap<String, Object>();
				HttpSession bksession = request.getSession();
				
				map.put("bdSeq", Integer.parseInt(bdSeq));
				BookListDomain boardListDomain =bookuploadService.bookSelectOne(map);
				List<BookFileDomain> fileList =  bookuploadService.bookSelectOneFile(map);
				
				for (BookFileDomain list : fileList) {
					String path = list.getUpFilePath().replaceAll("\\\\", "/");
					list.setUpFilePath(path);
				}

				fileListVO.setSeq(boardListDomain.getBdSeq());
				fileListVO.setContent(boardListDomain.getBdContent());
				fileListVO.setTitle(boardListDomain.getBdTitle());
				fileListVO.setIsEdit("edit");  // upload 재활용하기위해서
				
			
				mav.addObject("detail", boardListDomain);
				mav.addObject("files", fileList);
				mav.addObject("fileLen",fileList.size());
				
				mav.setViewName("book/bookEditList.html");
				return mav;
			}
			
			@PostMapping("bkeditSave")
			public ModelAndView bkeditSave(@ModelAttribute("fileListVO") BookListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
				ModelAndView mav = new ModelAndView();
				
				//저장
				bookuploadService.bookfileProcess(fileListVO, request, httpReq);
				
				mav = bkSelectOneCall(fileListVO, fileListVO.getSeq(),request);
				fileListVO.setContent(""); //초기화
				fileListVO.setTitle(""); //초기화
				mav.setViewName("book/bookList.html");
				return mav;
			}
			
			@GetMapping("bkremove")
			public ModelAndView bk_mbRemove(@RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
				ModelAndView mav = new ModelAndView();
				
				HttpSession bksession = request.getSession();
				HashMap<String, Object> map = new HashMap<String, Object>();
				List<BookFileDomain> fileList = null;
				if(bksession.getAttribute("files") != null) {						
					fileList = (List<BookFileDomain>) bksession.getAttribute("files");
				}

				map.put("bdSeq", Integer.parseInt(bdSeq));
				
				//내용삭제
				bookuploadService.bkContentRemove(map);

				for (BookFileDomain list : fileList) {
					list.getUpFilePath();
					Path filePath = Paths.get(list.getUpFilePath());
			 
			        try {
			        	
			            // 파일 물리삭제
			            Files.deleteIfExists(filePath); // notfound시 exception 발생안하고 false 처리
			            // db 삭제 
									bookuploadService.bkFileRemove(list);
						
			        } catch (DirectoryNotEmptyException e) {
									throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
				}

				//세션해제
				bksession.removeAttribute("files"); // 삭제
				mav = bkListCall();
				mav.setViewName("book/bookList.html");
				
				return mav;
			}


		//리스트 가져오기 따로 함수뺌
		public ModelAndView bkListCall() {
			ModelAndView mav = new ModelAndView();
			List<BookListDomain> bkitems = bookuploadService.bookList();
			mav.addObject("items", bkitems);
			return mav;
		}
		
		  // 좌측 메뉴 클릭시 보드화면 이동 (로그인된 상태)
		@RequestMapping(value = "bkList")
		public ModelAndView bkList() { 
			ModelAndView mav = new ModelAndView();
			List<BookListDomain> bkitems = bookuploadService.bookList();
			System.out.println("items ==> "+ bkitems);
			mav.addObject("items", bkitems);
			mav.setViewName("book/bookList.html");
			return mav; 
		};
		

		

}