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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.code.Code;
import com.co.kr.domain.BoardFileDomain2;
import com.co.kr.domain.BoardListDomain2;
import com.co.kr.exception.RequestException;
import com.co.kr.service.UploadService2;
import com.co.kr.vo.FileListVO2;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class FileListController2 {
	
	@Autowired
	private UploadService2 uploadService2;

	
	@PostMapping(value = "upload")
	public ModelAndView bdUpload2(FileListVO2 fileListVO2, MultipartHttpServletRequest request2, HttpServletRequest httpReq2) throws IOException, ParseException  {
		
		ModelAndView mav = new ModelAndView();
		int bdSeq2 = uploadService2.fileProcess2(fileListVO2, request2, httpReq2);
		fileListVO2.setContent(""); //초기화
		fileListVO2.setTitle(""); //초기화
		
		// 화면에서 넘어올때는 bdSeq String이라 string으로 변환해서 넣어즘
		mav = bdSelectOneCall2(fileListVO2, String.valueOf(bdSeq2),request2);
		mav.setViewName("book/bookList.html");
		return mav;
		
	}
	
	//리스트 하나 가져오기 따로 함수뺌
		public ModelAndView bdSelectOneCall2(@ModelAttribute("fileListVO") FileListVO2 fileListVO2, String bdSeq2, HttpServletRequest request2) {
			ModelAndView mav = new ModelAndView();
			HashMap<String, Object> map = new HashMap<String, Object>();
			HttpSession session = request2.getSession();
			
			map.put("bdSeq2", Integer.parseInt(bdSeq2));
			BoardListDomain2 boardListDomain2 =uploadService2.boardSelectOne2(map);
			System.out.println("boardListDomain"+boardListDomain2);
			List<BoardFileDomain2> fileList =  uploadService2.boardSelectOneFile2(map);
			
			for (BoardFileDomain2 list : fileList) {
				String path = list.getUpFilePath2().replaceAll("\\\\", "/");
				list.setUpFilePath2(path);
			}
			mav.addObject("detail2", boardListDomain2);
			mav.addObject("files2", fileList);

			//삭제시 사용할 용도
			session.setAttribute("files", fileList);
			return mav;
			}
			//detail
			@GetMapping("detail2")
		    public ModelAndView bdDetail2(@ModelAttribute("fileListVO") FileListVO2 fileListVO2, @RequestParam("bdSeq") String bdSeq2, HttpServletRequest request2) throws IOException {
				ModelAndView mav = new ModelAndView();
				//하나파일 가져오기
				mav = bdSelectOneCall2(fileListVO2, bdSeq2,request2);
				mav.setViewName("book/bookList.html");
				return mav;
			
		}
			
			@GetMapping("edit2")
			public ModelAndView edit2(FileListVO2 fileListVO2, @RequestParam("bdSeq") String bdSeq2, HttpServletRequest request2) throws IOException {
				ModelAndView mav = new ModelAndView();

				HashMap<String, Object> map = new HashMap<String, Object>();
				HttpSession session = request2.getSession();
				
				map.put("bdSeq2", Integer.parseInt(bdSeq2));
				BoardListDomain2 boardListDomain =uploadService2.boardSelectOne2(map);
				List<BoardFileDomain2> fileList =  uploadService2.boardSelectOneFile2(map);
				
				for (BoardFileDomain2 list : fileList) {
					String path = list.getUpFilePath2().replaceAll("\\\\", "/");
					list.setUpFilePath2(path);
				}

				fileListVO2.setSeq(boardListDomain.getBdSeq2());
				fileListVO2.setContent(boardListDomain.getBdContent2());
				fileListVO2.setTitle(boardListDomain.getBdTitle2());
				fileListVO2.setIsEdit("edit");  // upload 재활용하기위해서
				
			
				mav.addObject("detail2", boardListDomain);
				mav.addObject("files2", fileList);
				mav.addObject("fileLen2",fileList.size());
				
				mav.setViewName("book/bookEditList.html");
				return mav;
			}
			
			@PostMapping("editSave")
			public ModelAndView editSave2(@ModelAttribute("fileListVO") FileListVO2 fileListVO2, MultipartHttpServletRequest request2, HttpServletRequest httpReq2) throws IOException {
				ModelAndView mav = new ModelAndView();
				
				//저장
				uploadService2.fileProcess2(fileListVO2, request2, httpReq2);
				
				mav = bdSelectOneCall2(fileListVO2, fileListVO2.getSeq(),request2);
				fileListVO2.setContent(""); //초기화
				fileListVO2.setTitle(""); //초기화
				mav.setViewName("book/bookList.html");
				return mav;
			}
			
			@GetMapping("remove")
			public ModelAndView mbRemove2(@RequestParam("bdSeq") String bdSeq2, HttpServletRequest request2) throws IOException {
				ModelAndView mav = new ModelAndView();
				
				HttpSession session = request2.getSession();
				HashMap<String, Object> map = new HashMap<String, Object>();
				List<BoardFileDomain2> fileList = null;
				if(session.getAttribute("files") != null) {						
					fileList = (List<BoardFileDomain2>) session.getAttribute("files");
				}

				map.put("bdSeq", Integer.parseInt(bdSeq2));
				
				//내용삭제
				uploadService2.bdContentRemove2(map);

				for (BoardFileDomain2 list : fileList) {
					list.getUpFilePath2();
					Path filePath = Paths.get(list.getUpFilePath2());
			 
			        try {
			        	
			            // 파일 물리삭제
			            Files.deleteIfExists(filePath); // notfound시 exception 발생안하고 false 처리
			            // db 삭제 
									uploadService2.bdFileRemove2(list);
						
			        } catch (DirectoryNotEmptyException e) {
									throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
			        } catch (IOException e) {
			            e.printStackTrace();
			        }
				}

				//세션해제
				session.removeAttribute("files"); // 삭제
				mav = bdListCall2();
				mav.setViewName("book/bookList.html");
				
				return mav;
			}


		//리스트 가져오기 따로 함수뺌
		public ModelAndView bdListCall2() {
			ModelAndView mav = new ModelAndView();
			List<BoardListDomain2> items = uploadService2.boardList2();
			mav.addObject("items", items);
			return mav;
		}

}