package aptSE;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SearchEngineServer
 */
@WebServlet(
		description = "Server of the our search engine project", 
		urlPatterns = { 
				"/SearchEngineServerPath", 
				"/APTSE"
		})
public class SearchEngineServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchEngineServer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter Out=response.getWriter();
		String SearchWord=request.getParameter("SearchTextBox");
		String Header="<head>"+"<form method="+"\""+"GET"+"\""+ "action="+"\""+"SearchEngineServerPath"+"\""+" >"+ "<input  type="+"text"+" size="+"100"+"  value="+"\""+SearchWord+" \" " +" name="+"SearchTextBox"+">"+"<input value="+"\""+"Search "+"\""+ "type="+"\""+"submit"+"\""+" /> </form>";
		String DIV="<div style="+"\" "+"float: left;"+"\""+"><img src="+"../SmallDowarley.gif " +" width="+"200 "+ "height="+"100 " +"alt="+"12 "+"></div>";
		String URL=URLPrint("www.google.com","www.officialTest.com",SearchWord);
		URL+=URLPrint("www.google.com","www.RetrievedSite.com","Dola");
		Out.println(Header+DIV+URL+"</html");
		
	
	}
protected String URLPrint(String URLBlue,String URLGreen,String Caption)
{
	String ToPrint;
	ToPrint="<div style="+"\"" +" width: 100%; overflow: hidden;"+"\" "+"> ";
	ToPrint+="<font size="+" \" 5 \" >"+"<a href="+"\""+URLBlue+"\" >"+Caption+"</a></font>";
	ToPrint+="  <p><font face="+"\""+"verdana "+"\""+ "color="+"\""+"green"+"\""+" >"+URLGreen+"</font></p> </div>";
	return ToPrint;
}
}
