package br.edu.ufba.softvis.visminer.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.edu.ufba.softvis.visminer.persistence.handler.RepositoryDocumentHandler;

@WebServlet("/RepositoryServlet")
public class RepositoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private RepositoryDocumentHandler repositoryHandler = new RepositoryDocumentHandler();
	private PrintWriter out;

    public RepositoryServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		out = response.getWriter();				
		String action = request.getParameter("action");
		
		switch (action) {
			case "getAllByRepository":
				getAllByRepository();				
				break;
			default:
				break;
		}
	}
	
	private void getAllByRepository() {
		List<String> repositoryList = new ArrayList<>();
		repositoryHandler.getAll()
			.forEach(repository->repositoryList.add(repository.toJson()));
		out.println(repositoryList.toString());		
	}

}
