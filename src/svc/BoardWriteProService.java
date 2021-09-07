/*
 * '글 등록 요청'을 처리하는 비지니스 로직을 구현 Service클래스
 */

package svc;

import static db.Jdbcutil.close;
import static db.Jdbcutil.commit;
import static db.Jdbcutil.getConnection;
import static db.Jdbcutil.rollback;

import java.sql.Connection;

import dao.BoardDAO;
import vo는DTO.Boardbean;

//import db.Jdbcutil;

public class BoardWriteProService {
	// 기본생성자

	public boolean registArticle(Boardbean boardbean) {
		// 커넥션풀에서 DB 연결하기 위한 ★Connection객체만★를 얻오와
		// Jdbcutil.getConnection();//클래스명.static메서드 호출
		Connection con = getConnection();

		// BoardDAO : 싱글톤 패턴- 단 1개의 객체만 생성하여 공유!!
		BoardDAO boardDAO = BoardDAO.getInstance();

		// BoardDAO객체에서 DB작업을 할 때 사용 하도록 커넥션객체를 매개값으로 설정함!!
		// 실질적으로 db랑 연결하는거는 BoardDAO 에서 한다!!
		// 책에서는 커넥션 만들어 놓고 boa
		boardDAO.setConnection(con);

		// DB의 board테이블에 '사용자가 입력한 값들(BoardBean객체)' 글추가
		boolean isWriteSuccess = false;//글 등록 성공 여부 기본값은(실패=false를 들려줌)
		int insertCount = boardDAO.insertArticle(boardbean);// boardbean안에 고대로 사용자 글이 있다 -> 성공하면 1리턴 받는다
		if(insertCount > 0) { //추가가 성공 하면  '글 등록 성공' 이라고 한다 
			//insert,delect,뭐한개있는데 그거 하면 commit(con); 해야한다
			commit(con); //트랜잭션 완료
			isWriteSuccess = true;
			
		}else {
			rollback(con); //트랙잭션 취소 , 보낸걸 아니면 롤백해서 되돌아온다
		}
		
		close(con);
		return isWriteSuccess;
	}
}
