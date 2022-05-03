/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package servlets;

import jdk.nashorn.internal.parser.JSONParser;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonStructure;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.json.JsonReader;

/**
 * @author bishan
 */
@WebServlet(name = "TestServlet", loadOnStartup = 1, urlPatterns =
	{
		"/TestServlet"
	})
public class TestServlet extends HttpServlet
{

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException
	{

//    request.setAttribute("img", svg);
	}

	@Override
	protected void doPost(HttpServletRequest request,
												HttpServletResponse response)
		throws ServletException, IOException
	{

		response.setStatus(200);
		try (PrintWriter writer = response.getWriter())
		{
			try
			{
				JsonStructure jsonObj =
					Json.createReader(request.getInputStream()).readObject();
			} catch (Exception e)
			{
			}


			writer.append("hello");
		}
	}

}
