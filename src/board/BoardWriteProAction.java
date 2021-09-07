package board;

import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//파일업로드하기 위해서 cos.jar를 lib폴더에 추가
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import action.Action;
import svc.BoardWriteProService;
import vo는DTO.ActionForward;
import vo는DTO.Boardbean;


public class BoardWriteProAction implements Action {
	//ActionForward는 못 고친다!
	@Override
	public ActionForward exeute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ActionForward forward =null;
		
		//새로 등록할 글 정보를 저장할 BoardBean클래스
		
		//한 번에 업로드할 수 있는 파일 크기 5m
		int fileSize=5*1024*1024;
		
		//파일이 업로드 될 서버상의 실제 디렉토리(폴더) 경로
		ServletContext context = request.getServletContext();
		String realFolder = context.getRealPath("/boardUpload"); //실제 경로를 가져온다!
		
		
		MultipartRequest multi = new MultipartRequest(request, realFolder,fileSize,"UTF-8",
													new DefaultFileRenamePolicy());//예는 파일이름을 다시 설정하겠다
													//파일이름 중복처리를 위한 객체 (ex)a.txt	a1.txt로 자동변경하여 업로드
													//파일일므 중복처리를 위한객체(예)서버상에 이미 a.txt가 있으면 a1.txt로 자동변경된다!
		//새로 등록할 글 정보를 저장할 BoardBean클래스
		Boardbean boardbean = new Boardbean();
		
		//기본값으로 채워진 BoardBean객체를 ★사용자가 입력한★ 정보로 채움		
		boardbean.setBoard_name(multi.getParameter("board_name")); //★★★multi를 이용해서 이름 비번 등등 가져왔다★★
		boardbean.setBoard_pass(multi.getParameter("board_pass"));
		boardbean.setBoard_subject(multi.getParameter("board_subject"));
		boardbean.setBoard_content(multi.getParameter("board_content"));
		boardbean.setBoard_file(multi.getOriginalFileName((String)multi.getFileNames().nextElement()));
		//파일이름들을 가져오고.첫번체이름을 오리지날파일이름을 가져와서 보드빈에.setBOARD에 셋팅 시키겠다!!
		
		//새로운 글(boardBean)을 등록하는 BoardWriteProService 객체 생성 후
		BoardWriteProService boardWriteProService = new BoardWriteProService();
		
		//객체 안의 registArticle()메소드로 DB연결,BoardBean객체 DB의 BOARD테이블에 추가
		boolean isWriteSuccess = boardWriteProService.registArticle(boardbean); //true , false둘중 한개 맞아 밑에 껄 실행
		
		//추가 후 성공하면 true, 실패하면 false
		if(!isWriteSuccess) {//이말은 isWriteSuccess == flase 즉!! 글 등록이 실패 하면 !!
			response.setContentType("text/html;charset=UTF-8");//응답이 있는 것들은 html로 받아서 utf-8로 받는다!!
			PrintWriter out = response.getWriter();
			out.print("<script>");
			out.print("alert('등록실패');");
			out.print("history.back();");
			out.print("</script>");
			
		}else {//글 성공이 성공하면!!
			forward = new ActionForward();
			//forward.setRedirect(isRedirect);//기본값 false:디스페치방시 으로 할꺼면 굳이 할 필요 없다 post해놔서
			//아!!리다이렉트로 새로운 요청해야한다 왜!?
			//이미 글쓰기 끝났으닌깐
			//새로운 요청으로 가야한다!!
			forward.setRedirect(true);//true : 리다이렉트(새요청)으로 보내야한다!! (기본값false:디스패치)
			
			
			forward.setPath("boardList.bo");//글 전체 목록 보기!! 요청 하면  -> 다시 프론트컨트롤러로 이동!!후 처리 하는것
			
		}
		return forward;
		
			
	}
	

}
