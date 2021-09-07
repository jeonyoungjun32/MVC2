package svc;

import static db.Jdbcutil.close;
import static db.Jdbcutil.getConnection;

import java.sql.Connection;
import java.util.ArrayList;

import dao.BoardDAO;
import vo는DTO.Boardbean;

public class BoardListService { //서비스 = 
// 책 641p
	// 게시판의 총 글의 개수를 반환하는 메서드 이다!
	public int getListcount() throws Exception{
		// 커넥션풀에서 DB 연결하기 위한 ★Connection객체만★를 얻오와
				// Jdbcutil.getConnection();//클래스명.static메서드 호출
				Connection con = getConnection();//최종적으로 DAO에서 사용한다 
				//↑위 코드는 전역 객체 이므로 바로 사용가능하다!!

				// BoardDAO : 싱글톤 패턴- 단 1개의 객체만 생성하여 공유!!
				BoardDAO boardDAO = BoardDAO.getInstance();
				//getInstance = static만들어 놨다

				// BoardDAO객체에서 DB작업을 할 때 사용 하도록 커넥션객체를 매개값으로 설정함!!
				// 실질적으로 db랑 연결하는거는 BoardDAO 에서 한다!!
				// 책에서는 커넥션 만들어 놓고 boa
				boardDAO.setConnection(con);
				int listCount = boardDAO.selectListCount(); //실질적으로 연결하고 실행한다. BoardDAO에 만들었다
				
				close(con);
				
				return listCount;
	}

	//페이지 번호와 한 페이지당 출력될 글의 개수 10을 전송받아 '지정한 페이지'에 출력될 글 목록을 전달 하는데 '빈 객체를 이용해서 보낸다 = ArrayList<BoardBean>객체로 반환한다 ' 
	public ArrayList<Boardbean> getAritcleList(int page, int limit) {
		// TODO 비어있는객체에 담아 보낼라고!!
		Connection con = getConnection();
		BoardDAO boardDAO = BoardDAO.getInstance();
		boardDAO.setConnection(con);
		
		ArrayList<Boardbean> articleList = boardDAO.selectArticleList(page,limit);
		
		return articleList;
		
		
	}
}
