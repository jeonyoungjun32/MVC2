<!-- 전체 게시판 글목록을 보여주는 뷰페이지	 -->
<%@page import="vo는DTO.PageInfo"%>
<%@page import="vo는DTO.*" %>
<%@page import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%
 //request영역에 공유한 pageInfo와 articleList속성값을 얻어옴
 PageInfo pageInfo = (PageInfo)request.getAttribute("pageInfo");
 ArrayList<Boardbean> articleList = (ArrayList<Boardbean>)request.getAttribute("articleList");
 
 //페이징 처리 관련 정보를 사용하기 편하도록 각 변수에 저장함
 int nowPage = pageInfo.getPage();//페이지번호
 int listCount = pageInfo.getListCount();//게시판의 총 글의 개수
 int maxPage = pageInfo.getMaxPage();//총 페이지수
 int startpage = pageInfo.getStartPage();//현재 페이지에 보여줄 시작 페이지 수
 int endPage = pageInfo.getEndPage();//현재 페이지에 보여줄 마지막 페이지 수 
 %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<style type="text/css">
h2{
text-align: center;
}
table {
	
}
#tr_top{
	border: orange;
}
#pageList{ /* 아래쪽 페이지 번호 */
	margin: auto;
	width: 500px;
	text-align: center;
}
#emptyArea{
margin: auto;
width: 500px;
text-align: center;
}
</style>
</head>
<body>
<section>
<h2>글목록 <a href="boardWriteForm.bo">게시판 글쓰기</a></h2>
<table>
<%if(articleList != null && listCount > 0){ %>
	<tr id="tr_top">
		<th>번호</th>
		<th>제목</th>
		<th>작성자</th>
		<th>날짜</th>
		<th>조회수ㅋ</th>
	</tr>
	<%for(int i=0;i<articleList.size();i++){ %>
	<tr>
		<td><%=articleList.get(i).getBoard_num() %></td><!-- 글번호 -->
		<!-- 해당글이 답변글일 경우 들여쓰기 + ▶ + 제목, 원글이면▶+제목-->
		<td>
			<%if(articleList.get(i).getBoard_re_lev()!=0){ %><!-- 답변글이면 -->
				<!-- 들여쓰기 -->
				<%for(int j=0;j<articleList.get(i).getBoard_re_lev()*2;j++) {%>
					&nbsp;<!--board_re_lev*2만큼 들여쓰기한 다음 ▶출력 -->
				<%}//for %>▶
			<%}else{ %><!-- 답변글이 아니라 원글이면 ▶만 출력 -->
				▶
			<%} %>
		
		<!-- 글 제목 클릭하면 글 내용 상세보기 요청을 할 수 있도록 링크
		board_num(글번호) 전송 이유? 링크를 클릭했을 때 선택된 글 내용을 뵤여줘야 하므로
		page(페이지 번호) 정송 이유? 글 내용을 본 후 다시 원레 보던 페이지로 돌아가야 하므로-->
		<a href="boardDetail.bo?board_num=<%=articleList.get(i).getBoard_subject() %>&page=<%=nowPage%>"><%=articleList.get(i).getBoard_subject() %></a>
		</td>		
		<td><%=articleList.get(i).getBoard_name() %></td><!-- 작성자 -->		
		<td><%=articleList.get(i).getBoard_date() %></td><!-- 날짜 -->		
		<td><%=articleList.get(i).getBoard_readcount() %></td><!-- 조히수 -->		
	</tr>
	<%}//for %>
</table>
</section>
<!-- 페이징 처리를 위해서 페이지 번호 출력 -->
<section id ="pageList">
	<%if(nowPage <= 1){%>  [이전]&nbsp;
	<%}else{ %>	<a href="boardList.bo?page=<%=nowPage-1%>">[이전]</a>	&nabsp;		<%} %>
	
	<%for(int i=startpage;i<=endPage;i++){
		if(i == nowPage){%>[<%=i %>]&nabsp;
		<%}else{%>
			<a href="boardList.bo?page=<%=i %>" >[<%=i %>]</a>&nabsp;
		<%}//else끝 %>
	<%} //for문 끝 %>
	
	<%if(nowPage >= maxPage) {%> [다음]
	<%}else{ %>
		<a href="boardList.bo?page=<%=nowPage+1 %>">[다음]</a>
	<%} %>
</section>

<% } else{%>
	<section>등록된 글이 없습니다.</section> 
<% }//if문 끝%>
	
</body>
</html>










