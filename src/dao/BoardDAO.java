//DB로 SQL구문을 전송하는 클래스
package dao;

import static db.Jdbcutil.close;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import vo는DTO.Boardbean;

public class BoardDAO {
	Connection con = null;//멤버변수(전역변수 : 전체 메서드에서 사용 가능)
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	/* 싱글톤 패턴 : BoardDAO객체 단 1개만 생성
	 * 이유? 외부 클래스에서 "처음 생성된 BoardDAO객체를 공유해서 사용하도록 하기 위해" 
	 */
	private BoardDAO(){}
	
	private static BoardDAO boardDAO;
	//static이유? 객체를 생성하기 전에 이미 메모리에 올라간 getInstance()메서드를 통해서만 BoardDAO객체를 1개만 만들도록 하기 위해
	public static BoardDAO getInstance(){
		if(boardDAO == null) {//객체가 없으면
			boardDAO = new BoardDAO();//객체 생성
		}
		
		return boardDAO;//기존 객체의 주소 리턴
	}
	
	public void setConnection(Connection con){//Connection객체를 받아 DB 연결
		this.con=con;
	}
	
	//1.글 등록
	public int insertArticle(Boardbean article){
		//PreparedStatement pstmt = null;
		//ResultSet rs = null;
		int num=0;
		String sql="";
		int insertCount=0;		
		
		try {
			//pstmt=con.prepareStatement("select max(board_num) from board");//교재
			                            //오라클:NVL(), NVL2()
			pstmt=con.prepareStatement("select IFNULL(max(board_num),0)+1 from board");//수정
			rs = pstmt.executeQuery();
			
			//if(rs.next()) num=rs.getInt(1)+1;//10+1=11. 교재
			//else num=1;//null이면 1. 교재
			
			if(rs.next()) num=rs.getInt(1);//수정
			
			sql +="insert into board values(?,?,?,?,?,?,?,?,?,?,now())"; //now()=오라클 sysdate (★주의:()없음)
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.setString(2, article.getBoard_name());//글쓴이
			pstmt.setString(3, article.getBoard_pass());//비밀번호
			pstmt.setString(4, article.getBoard_subject());//제목
			pstmt.setString(5, article.getBoard_content());//내용
			pstmt.setString(6, article.getBoard_file());//첨부 파일
			
			pstmt.setInt(7, num);//답변글 등록할 때 '원글과 답변글'을 '같은그룹'으로 묶기 위해 num사용함(그룹번호가 같으면 같은 그룹임)
			pstmt.setInt(8, 0);//얼마만큼 안쪽으로 들어가 글이 시작될 것인지를 결정해 주는 값(0으로 초기화. 0은 원글)답변글을 달면 1씩 증가한다
			pstmt.setInt(9, 0);//원글에서 답변글이 몇 번째 아래에 놓일 것인지 위치를 결정해 주는 값(0으로 초기화) 답변글을 달면 1씩 증가한다
			
			pstmt.setInt(10, 0);//조회수(0으로 초기화)
			//now()는 11 이며 오늘날짜를 뜻한다
			
			insertCount=pstmt.executeUpdate();//업데이트가 성공하면 1을 리턴받음
		} catch (Exception e) {			
			//e.printStackTrace();
			System.out.println("boardInsert 에러 :" + e);//e:예외종류+예외메세지
		} finally {
			close(rs);
			close(pstmt);			
		}
		
		return insertCount;
	}
	
	
	//2.게시판 전체 글의 개수 구하여 반환
	public int selectListCount(){
		int listCount = 0;
		try {
			pstmt = con.prepareStatement("select count(*) from board");
			rs = pstmt.executeQuery();
			
			if(rs.next()) listCount = rs.getInt(1);//조회한 전체 글의 수
			
		} catch (Exception e) {			
			//e.printStackTrace();
			System.out.println("getListCount 에러 :" + e);//e:예외종류+예외메세지
		} finally {
			close(rs);
			close(pstmt);			
		}
		
		return listCount;
	}

	public ArrayList<Boardbean> selectArticleList(int page, int limit) {
		 ArrayList<Boardbean> articleList = new ArrayList<Boardbean>();
		/* BOARD_RE_REF : 같은 수는 같은 그룹
		 *                (원글이 글번호가 3이면 답변글도 모두 3)
		 *                
		 * BOARD_RE_SEQ : 원글에서 답변글이 몇 번째 아래에 놓일 것인지 위치를 결정해 주는 값               
		 */
		
		                                                            //limit 10,10 :11행부터 10개의 행 
		String sql="select * from board order by BOARD_RE_REF desc, BOARD_RE_SEQ asc limit ?,10";
													//BOARD_RE_REF = 18번 글에 18번 뎃글을 적을수 있다 	
													//BOARD_RE_SEQ = 0, 1, 2, 
		/*
		 * startrow변수에 해당 페이지에서 출력되어야 하는 시작 레코드의 index번호를 구하여 저장
		 * (예)아래 페이지 번호 중 2를 클릭하면 page가 2가 되어 (2-1)*10=10
		 */
		
		int startrow=(page-1)*10;//10이지만 읽기 시작하는 row번호는 11이 됨
		
		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, startrow);//11부터 10개의 레코드 조회(답변글 포함해서)
			rs=pstmt.executeQuery();
			
			Boardbean boardBean = null;
			while(rs.next()) {
				boardBean=new Boardbean();//기본값으로 채워진 객체를 조회한 결과값으로 변경함
				
				boardBean.setBoard_num(rs.getInt("BOARD_NUM"));//글 번호
				boardBean.setBoard_name(rs.getString("BOARD_NAME"));//글 작성자 (★주의 : 글 비밀번호 제외)
				boardBean.setBoard_subject(rs.getString("BOARD_SUBJECT"));//글 제목
				boardBean.setBoard_content(rs.getString("BOARD_CONTENT"));//글 내용
				boardBean.setBoard_file(rs.getString("BOARD_FILE"));//첨부파일
				
				boardBean.setBoard_re_ref(rs.getInt("BOARD_RE_REF"));//관련글 번호
				boardBean.setBoard_re_lev(rs.getInt("BOARD_RE_LEV"));//답글 레벨
				boardBean.setBoard_re_sql(rs.getInt("BOARD_RE_SEQ"));//관련글 중 출력순서
				
				boardBean.setBoard_readcount(rs.getInt("BOARD_READCOUNT"));//조회수
				boardBean.setBoard_date(rs.getDate("BOARD_DATE"));//작성일
				
				articleList.add(boardBean);
			}
		}  catch (Exception e) {			
			//e.printStackTrace();
			System.out.println("articleList 에러 :" + e);//e:예외종류+예외메세지
		} finally {
			close(rs);
			close(pstmt);			
		}
		
		return articleList;
	}

}
