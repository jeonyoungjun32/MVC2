package board;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import action.Action;
import svc.BoardListService;
import vo는DTO.ActionForward;
import vo는DTO.Boardbean;
import vo는DTO.PageInfo;

public class BoardListAction implements Action {
//649p 그림 참조!
	@Override
	public ActionForward exeute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub

		BoardListService boardListService = new BoardListService();
		int listCount = boardListService.getListcount();// 게시판의 총 글의 개수를 얻어오고

		// 이제 '클릭한 페이지 번호' 와 '한 페치이당 출력됨 글의 개수 10' 을 매개값으로 전송하여 출력될 글 목록을 얻어옴
		int page = 1; // 출력될 페이지의 기본값으로 1페이지 절정!
		int limit = 10; // 한 페이지당 출력될 글의 개수를 10개로 설정!
		
		if(request.getParameter("page") != null); {//"2"라는 문자열로 왔기에 
			//null이 아니면 출력될 페이지가 파라미터로 전송되었으면
			page = Integer.parseInt(request.getParameter("page"));//문자열로 온 page를 연산할 수 있도록 int형으로 강제형변환시킨다 
		}
		

		// listCount = 총 페이지 수 계산 (예) 11.0/10 = 1.1+0.95 =2.5 =>2페이지됌, 21.0/10=2.1+0.95 =3.05=>3페이지됌 
		int maxPage = (int)((double)listCount/limit + 0.95);
		
		//뷰 페이지에서 할수 없다!!
		/*****************
		 * 책 649p 참고!!
		 * 현재 페이지에 보여줄 싲가 페이지 수(1)
		 * [이전]1  2  3  4  5  6  7  8  9  10 [다음] 을 보여주고 !!
		 * [이전]11 12 13 14 15 16 17 18 19 20 [다음] 을 보여주고 !!
		 * 
		 * */
		//page가 2일때 : 2.0/10=0-2+0.9 =1.1을 double을 만나 1이 출력됌!
		int startPage =(((int)((double)page/10+0.9))-1)*10+1;//시발 이렇게 하면 시작 페이지가 나온닼ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ
		
		//page가 15일때 : 15.0/10 =1.5+0.9 =2.4을 2-1=1*10+1=11 [이전]11 12 13 14 15 ...20 [다음]

		
		//현재 페이지에 보여줄 마지막 페이지 수(10,20,30 등)
									//startPage=11일때 : 11+10-1=20
		int endPage = startPage+10-1;//스타트페이지가 1이면  1+10-1=10 이렇게 나온다 
		/*
		 * 전체 페이지가 15라면 [이전]11 12 13 14 15 [다음]
		 * endPage(20)이라면?
		 * endPage(20) > 총페이지 수(15)보다 크면 endPage=15로 설정
		 */
		if(endPage > maxPage) {//endPage가 크다면 maxPage로 바꿔저야된다!
			endPage = maxPage; 
			
			/*
			 * 위에서 구한 페이징에 관한 정보를 저장할 PageInfo객체(DTO) 생성후!!
			 * 디스페치 방식으로 포워딩한다!
			 */
		}
		 
		PageInfo pageInfo =  new PageInfo();
		pageInfo.setPage(page); 		//페이지 번호
		pageInfo.setListCount(listCount);//게시판의 총 글의 개수
		pageInfo.setMaxPage(maxPage);	//총 페이지 수
		pageInfo.setStartPage(startPage);//현재 페이지에 보여줄 시작 페이지 수
		pageInfo.setEndPage(endPage);	//현재 페이지에 보여줄 마지막 페이지수 	
		
		
		
		//클릭한 페이지 번호 와 한페이지당 출력도리 글 개수 10개 을 매개값으로 전송하여 '해당 페이지의 출력될 글'
		ArrayList<Boardbean> articleList = boardListService.getAritcleList(page, limit);
		
		/*
		 * 1.	위에서 구한 페이징에 권한 정보를 저장한 PageInfo객체(DTO)와
		 * 2.	출력될 글 목록 정보가 담긴 ArrayList<Boardbean>객체를 
		 * 3.	미리 request영역에 공유시킨 후 
		 * 4.	디스패치 방식(기존의 요청 그대로)으로 포워딩함
		 * 
		 */
		
		request.setAttribute("pageInfo", pageInfo); //"페이지인포" 안에 pageInfo가 들어가있다
		request.setAttribute("articleList", articleList); 
		//자 포워딩 해보자!~
		//포워딩 객체 만들고~
		ActionForward forward = new ActionForward();
		forward.setPath("/board/qna_board_list.jsp");
		
		
		
		
		
		
		return forward;
	}
	
	

}

















