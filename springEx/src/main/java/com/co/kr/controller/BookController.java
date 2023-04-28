//package com.co.kr.controller;
//
//
//import java.io.IOException;
//import java.nio.file.DirectoryNotEmptyException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.text.ParseException;
//import java.util.HashMap;
//import java.util.List;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartHttpServletRequest;
//import org.springframework.web.servlet.ModelAndView;
//
//import com.co.kr.code.Code;
//import com.co.kr.domain.BoardFileDomain;
//import com.co.kr.domain.BoardListDomain;
//import com.co.kr.domain.BookFileDomain;
//import com.co.kr.domain.BookListDomain;
//import com.co.kr.exception.RequestException;
//import com.co.kr.service.BookUploadService;
//import com.co.kr.service.UploadService;
//import com.co.kr.vo.BookListVO;
//import com.co.kr.vo.FileListVO;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Controller
//public class BookController {
//	
//	@Autowired
//	private BookUploadService uploadService;
//
//	
//	@PostMapping(value = "upload")
//	public ModelAndView bdUpload(BookListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException, ParseException  {
//		
//		ModelAndView mav = new ModelAndView();
//		int bdSeq = uploadService.fileProcess(fileListVO, request, httpReq);
//		fileListVO.setContent(""); //초기화
//		fileListVO.setTitle(""); //초기화
//		
//		// 화면에서 넘어올때는 bdSeq String이라 string으로 변환해서 넣어즘
//		mav = bdSelectOneCall(fileListVO, String.valueOf(bdSeq),request);
//		mav.setViewName("book/bookList.html");
//		return mav;
//		
//	}
//	
//	//리스트 하나 가져오기 따로 함수뺌
//		public ModelAndView bdSelectOneCall(@ModelAttribute("fileListVO") BookListVO fileListVO, String bdSeq, HttpServletRequest request) {
//			ModelAndView mav = new ModelAndView();
//			HashMap<String, Object> map = new HashMap<String, Object>();
//			HttpSession session = request.getSession();
//			
//			map.put("bdSeq", Integer.parseInt(bdSeq));
//			BookListDomain boardListDomain =uploadService.boardSelectOne(map);
//			System.out.println("boardListDomain"+boardListDomain);
//			List<BookFileDomain> fileList =  uploadService.boardSelectOneFile(map);
//			
//			for (BookFileDomain list : fileList) {
//				String path = list.getUpFilePath().replaceAll("\\\\", "/");
//				list.setUpFilePath(path);
//			}
//			mav.addObject("detail", boardListDomain);
//			mav.addObject("files", fileList);
//
//			//삭제시 사용할 용도
//			session.setAttribute("files", fileList);
//			return mav;
//			}
//			//detail
//			@GetMapping("detail")
//		    public ModelAndView bdDetail(@ModelAttribute("fileListVO") BookListVO fileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
//				ModelAndView mav = new ModelAndView();
//				//하나파일 가져오기
//				mav = bdSelectOneCall(fileListVO, bdSeq,request);
//				mav.setViewName("book/bookList.html");
//				return mav;
//			
//		}
//			
//			@GetMapping("edit")
//			public ModelAndView edit(FileListVO fileListVO, @RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
//				ModelAndView mav = new ModelAndView();
//
//				HashMap<String, Object> map = new HashMap<String, Object>();
//				HttpSession session = request.getSession();
//				
//				map.put("bdSeq", Integer.parseInt(bdSeq));
//				BookListDomain boardListDomain =uploadService.boardSelectOne(map);
//				List<BookFileDomain> fileList =  uploadService.boardSelectOneFile(map);
//				
//				for (BookFileDomain list : fileList) {
//					String path = list.getUpFilePath().replaceAll("\\\\", "/");
//					list.setUpFilePath(path);
//				}
//
//				fileListVO.setSeq(boardListDomain.getBdSeq());
//				fileListVO.setContent(boardListDomain.getBdContent());
//				fileListVO.setTitle(boardListDomain.getBdTitle());
//				fileListVO.setIsEdit("edit");  // upload 재활용하기위해서
//				
//			
//				mav.addObject("detail", boardListDomain);
//				mav.addObject("files", fileList);
//				mav.addObject("fileLen",fileList.size());
//				
//				mav.setViewName("book/bookEditList.html");
//				return mav;
//			}
//			
//			@PostMapping("editSave")
//			public ModelAndView editSave(@ModelAttribute("fileListVO") BookListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
//				ModelAndView mav = new ModelAndView();
//				
//				//저장
//				uploadService.fileProcess(fileListVO, request, httpReq);
//				
//				mav = bdSelectOneCall(fileListVO, fileListVO.getSeq(),request);
//				fileListVO.setContent(""); //초기화
//				fileListVO.setTitle(""); //초기화
//				mav.setViewName("book/bookList.html");
//				return mav;
//			}
//			
//			@GetMapping("remove")
//			public ModelAndView mbRemove(@RequestParam("bdSeq") String bdSeq, HttpServletRequest request) throws IOException {
//				ModelAndView mav = new ModelAndView();
//				
//				HttpSession session = request.getSession();
//				HashMap<String, Object> map = new HashMap<String, Object>();
//				List<BookFileDomain> fileList = null;
//				if(session.getAttribute("files") != null) {						
//					fileList = (List<BookFileDomain>) session.getAttribute("files");
//				}
//
//				map.put("bdSeq", Integer.parseInt(bdSeq));
//				
//				//내용삭제
//				uploadService.bdContentRemove(map);
//
//				for (BookFileDomain list : fileList) {
//					list.getUpFilePath();
//					Path filePath = Paths.get(list.getUpFilePath());
//			 
//			        try {
//			        	
//			            // 파일 물리삭제
//			            Files.deleteIfExists(filePath); // notfound시 exception 발생안하고 false 처리
//			            // db 삭제 
//									uploadService.bdFileRemove(list);
//						
//			        } catch (DirectoryNotEmptyException e) {
//									throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
//			        } catch (IOException e) {
//			            e.printStackTrace();
//			        }
//				}
//
//				//세션해제
//				session.removeAttribute("files"); // 삭제
//				mav = bdListCall();
//				mav.setViewName("book/bookList.html");
//				
//				return mav;
//			}
//
//
//		//리스트 가져오기 따로 함수뺌
//		public ModelAndView bdListCall() {
//			ModelAndView mav = new ModelAndView();
//			List<BookListDomain> items = uploadService.boardList();
//			mav.addObject("items", items);
//			return mav;
//		}
//		
//		  // 좌측 메뉴 클릭시 보드화면 이동 (로그인된 상태)
//		@RequestMapping(value = "bdList")
//		public ModelAndView bdList() { 
//			ModelAndView mav = new ModelAndView();
//			List<BookListDomain> items = uploadService.boardList();
//			System.out.println("items ==> "+ items);
//			mav.addObject("items", items);
//			mav.setViewName("book/bookList.html");
//			return mav; 
//		};
//		
//
//		
//
//}