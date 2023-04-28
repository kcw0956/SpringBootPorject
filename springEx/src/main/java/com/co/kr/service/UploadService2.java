package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardFileDomain2;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.BoardListDomain2;
import com.co.kr.vo.FileListVO2;

public interface UploadService2 {
	
	// 전체 리스트 조회
		public List<BoardListDomain2> boardList2();
		
		// 인서트 및 업데이트
		public int fileProcess2(FileListVO2 fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq);
		
		// 하나 삭제
		public void bdContentRemove2(HashMap<String, Object> map);
		
		// 하나 삭제
		public void bdFileRemove2(BoardFileDomain2 boardFileDomain);
		
		// 하나 리스트 조회
		public BoardListDomain2 boardSelectOne2(HashMap<String, Object> map);
		// 하나 파일 리스트 조회
		public List<BoardFileDomain2> boardSelectOneFile2(HashMap<String, Object> map);

		

}
