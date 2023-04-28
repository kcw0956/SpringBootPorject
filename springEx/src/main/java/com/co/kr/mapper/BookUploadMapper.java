//package com.co.kr.mapper;
//
//import java.util.HashMap;
//import java.util.List;
//
//import org.apache.ibatis.annotations.Mapper;
//
//import com.co.kr.domain.BoardContentDomain;
//import com.co.kr.domain.BoardFileDomain;
//import com.co.kr.domain.BoardListDomain;
//import com.co.kr.domain.BookContentDomain;
//import com.co.kr.domain.BookFileDomain;
//import com.co.kr.domain.BookListDomain;
//
//
//@Mapper
//public interface BookUploadMapper {
//
//	// 전체 리스트 조회
//		public List<BookListDomain> boardList();
//		//content insert
//		public void contentUpload(BookContentDomain boardContentDomain);
//		//file insert
//		public void fileUpload(BookFileDomain boardFileDomain);
//
//		//content update
//		public void bdContentUpdate(BookContentDomain boardContentDomain);
//		//file updata
//		public void bdFileUpdate(BookFileDomain boardFileDomain);
//
//	  //content delete 
//		public void bdContentRemove(HashMap<String, Object> map);
//		//file delete 
//		public void bdFileRemove(BookFileDomain boardFileDomain);
//		
//		//select one
//		public BookListDomain boardSelectOne(HashMap<String, Object> map);
//
//		//select one file
//		public List<BookFileDomain> boardSelectOneFile(HashMap<String, Object> map);
//
//}
