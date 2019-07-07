package com.Cc.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import com.Cc.pojo.Student;
import com.Cc.pojo.Teacher;
import com.Cc.service.StudentService;
import com.Cc.util.SavePathUtil;

@MultipartConfig // �ļ��ϴ���ע��
@WebServlet("/StudentServlet")
public class StudentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String choose = request.getParameter("choose");
		switch (choose) {
		case "register":
			register(request, response);
			break;
		case "login":
			login(request, response);
			break;
		case "loginOut":
			// ֻ��Ҫ���session���û���
			HttpSession session = request.getSession();
			session.removeAttribute("studentName");
			session.invalidate();// ʧЧ
			response.sendRedirect("/Cloud_class/front/login.jsp");
			break;

		default:
			break;
		}
	}


	private void login(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String password = request.getParameter("password");
		String yzm = request.getParameter("yzm");

		String checkcode_session = (String) request.getSession().getAttribute("checkcode_session");
		System.out.println("�������֤��:" + yzm);
		System.out.println("ˢ�µ���֤��:" + checkcode_session);
		// ��ֵ�����ж� �����Ƿ��¼�ɹ�
		System.out.println(id + "," + password);
		StudentService ss = new StudentService();
		Student student = ss.login(id, password);
		try {
			if (student != null && yzm.equalsIgnoreCase(checkcode_session)) {
				System.out.println("��¼�ɹ�");
				// ����¼�ɹ����û���������session��
				HttpSession session = request.getSession();
				String studentName = student.getStu_name();
				session.setAttribute("student", student);// �Լ�ֵ�Դ���
				request.getRequestDispatcher("MsgServlet?flag=List2").forward(request, response);
			} else {
				System.out.println("��¼ʧ��");
				response.sendRedirect(request.getContextPath() + "/front/login.jsp");
			}
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void register(HttpServletRequest request, HttpServletResponse response) {
		// ��ȡѧ����ͷ��ͼƬ
		String img = null;
		try {
			Part part = request.getPart("img");
			System.out.println(part);
			String clientName = part.getSubmittedFileName();
			String randomName = SavePathUtil.randomFileName(clientName);

			// ����·��
			part.write(SavePathUtil.createDir("D:\\upload") + "\\" + randomName);
			// �洢�����ݿ�·��
			img = "upload\\" + randomName;

			System.out.println(img);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ServletException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String Id = request.getParameter("userName");
		String password = request.getParameter("password");
		String name = request.getParameter("trueName");
		String sex = request.getParameter("sex");
		String tel = request.getParameter("tel");
		int id = Integer.parseInt(Id);
		Student student = new Student(id, password, name, tel, sex, img, 0);
		StudentService ss = new StudentService();
		int n = ss.register(student);
		try {
			if (n > 0) {
				request.getRequestDispatcher("StudentServlet?choose=login").forward(request, response);
			} else {
				response.sendRedirect(request.getContextPath() + "/front/reg.jsp");
			}
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
