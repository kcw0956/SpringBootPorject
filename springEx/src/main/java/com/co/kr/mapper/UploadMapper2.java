package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.BoardContentDomain;
import com.co.kr.domain.BoardContentDomain2;
import com.co.kr.domain.BoardFileDomain;
import com.co.kr.domain.BoardFileDomain2;
import com.co.kr.domain.BoardListDomain;
import com.co.kr.domain.BoardListDomain2;


@Mapper
public interface UploadMapper2 {

	// 전체 리스트 조회
		public List<BoardListDomain2> boardList2();
		//content insert
		public void contentUpload2(BoardContentDomain2 boardContentDomain);
		//file insert
		public void fileUpload2(BoardFileDomain2 boardFileDomain);

		//content update
		public void bdContentUpdate2(BoardContentDomain2 boardContentDomain);
		//file updata
		public void bdFileUpdate2(BoardFileDomain2 boardFileDomain);

	  //content delete 
		public void bdContentRemove2(HashMap<String, Object> map);
		//file delete 
		public void bdFileRemove2(BoardFileDomain2 boardFileDomain);
		
		//select one
		public BoardListDomain2 boardSelectOne2(HashMap<String, Object> map);

		//select one file
		public List<BoardFileDomain2> boardSelectOneFile2(HashMap<String, Object> map);

}
