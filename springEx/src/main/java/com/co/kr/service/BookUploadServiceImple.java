package com.co.kr.service;


import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.code.Code;
import com.co.kr.domain.BookContentDomain;
import com.co.kr.domain.BookFileDomain;
import com.co.kr.domain.BookListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.BookUploadMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.BookListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class BookUploadServiceImple implements BookUploadService {

	@Autowired
	private BookUploadMapper bkuploadMapper;
	
	@Override
	public List<BookListDomain> bookList() {
		// TODO Auto-generated method stub
		return bkuploadMapper.bookList();
	}

	@Override
	public int bookfileProcess(BookListVO bkfileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) {
		//session 생성
		HttpSession bksession = httpReq.getSession();
		
		//content domain 생성 
		BookContentDomain boardContentDomain = BookContentDomain.builder()
				.mbId(bksession.getAttribute("id").toString())
				.bdTitle(bkfileListVO.getTitle())
				.bdContent(bkfileListVO.getContent())
				.build();
		
				if(bkfileListVO.getIsEdit() != null) {
					boardContentDomain.setBdSeq(Integer.parseInt(bkfileListVO.getSeq()));
					System.out.println("수정업데이트");
					// db 업데이트
					bkuploadMapper.bkContentUpdate(boardContentDomain);
				}else {	
					// db 인서트
					bkuploadMapper.bookcontentUpload(boardContentDomain);
					System.out.println(" db 인서트");

				}
				
				// file 데이터 db 저장시 쓰일 값 추출
				int bdSeq = boardContentDomain.getBdSeq();
				String mbId = boardContentDomain.getMbId();
				
				//파일객체 담음
				List<MultipartFile> multipartFiles = request.getFiles("files");
				
				
				// 게시글 수정시 파일관련 물리저장 파일, db 데이터 삭제 
				if(bkfileListVO.getIsEdit() != null) { // 수정시 

	
					List<BookFileDomain> bkfileList = null;
					
					
					
					for (MultipartFile multipartFile : multipartFiles) {
						
						if(!multipartFile.isEmpty()) {   // 수정시 새로 파일 첨부될때 세션에 담긴 파일 지우기
							
							
							if(bksession.getAttribute("files") != null) {	

								bkfileList = (List<BookFileDomain>) bksession.getAttribute("files");
								
								for (BookFileDomain list : bkfileList) {
									list.getUpFilePath();
									Path filePath = Paths.get(list.getUpFilePath());
							 
							        try {
							        	
							            // 파일 삭제
							            Files.deleteIfExists(filePath); // notfound시 exception 발생안하고 false 처리
							            //삭제 
										bkFileRemove(list); //데이터 삭제
										
							        } catch (DirectoryNotEmptyException e) {
										throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
							        } catch (IOException e) {
							            e.printStackTrace();
							        }
								}
								

							}
							
							
						}

					}
					
					
				}
				
				
				///////////////////////////// 새로운 파일 저장 ///////////////////////
				
				// 저장 root 경로만들기
				Path rootPath = Paths.get("/", "Users", "kcw0956", "upload").toAbsolutePath().normalize();
				File pathCheck = rootPath.toFile();
				
				// folder chcek
				if(!pathCheck.exists()) pathCheck.mkdirs();
				
				
	
				for (MultipartFile multipartFile : multipartFiles) {
					
					if(!multipartFile.isEmpty()) {  // 파일 있을때 
						
						//확장자 추출
						String originalFileExtension;
						String contentType = multipartFile.getContentType();
						String origFilename = multipartFile.getOriginalFilename();
						
						//확장자 조재안을경우
						if(ObjectUtils.isEmpty(contentType)){
							break;
						}else { // 확장자가 jpeg, png인 파일들만 받아서 처리
							if(contentType.contains("image/jpeg")) {
								originalFileExtension = ".jpg";
							}else if(contentType.contains("image/png")) {
								originalFileExtension = ".png";
							}else {
								break;
							}
						}
						
						//파일명을 업로드한 날짜로 변환하여 저장
						String uuid = UUID.randomUUID().toString();
						String current = CommonUtils.currentTime();
						String newFileName = uuid + current + originalFileExtension;
						
						//최종경로까지 지정
						Path targetPath = rootPath.resolve(newFileName);
						
						File file = new File(targetPath.toString());
						
						try {
							//파일복사저장
							multipartFile.transferTo(file);
							// 파일 권한 설정(쓰기, 읽기)
							file.setWritable(true);
							file.setReadable(true);
							
							
							//파일 domain 생성 
							BookFileDomain boardFileDomain = BookFileDomain.builder()
									.bdSeq(bdSeq)
									.mbId(mbId)
									.upOriginalFileName(origFilename)
									.upNewFileName("resources/upload/"+newFileName) // WebConfig에 동적 이미지 폴더 생성 했기때문
									.upFilePath(targetPath.toString())
									.upFileSize((int)multipartFile.getSize())
									.build();
							
								// db 인서트
								bkuploadMapper.bookfileUpload(boardFileDomain);
								System.out.println("upload done");
							
						} catch (IOException e) {
							throw RequestException.fire(Code.E404, "잘못된 업로드 파일", HttpStatus.NOT_FOUND);
						}
					}

				}
				
		
				return bdSeq; // 저장된 게시판 번호
	}

	@Override
	public void bkContentRemove(HashMap<String, Object> map) {
		bkuploadMapper.bkContentRemove(map);
	}

	@Override
	public void bkFileRemove(BookFileDomain boardFileDomain) {
		bkuploadMapper.bkFileRemove(boardFileDomain);
	}

	// 하나만 가져오기
	@Override
	public BookListDomain bookSelectOne(HashMap<String, Object> map) {
		return bkuploadMapper.bookSelectOne(map);
	}

	// 하나 게시글 파일만 가져오기
	@Override
	public List<BookFileDomain> bookSelectOneFile(HashMap<String, Object> map) {
		return bkuploadMapper.bookSelectOneFile(map);
	}

}