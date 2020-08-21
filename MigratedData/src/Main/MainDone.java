package Main;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MainDone extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public MainDone() 
    {
        // TODO Auto-generated constructor stub
    }

	
	public void init(ServletConfig config) throws ServletException 
	{
	
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String msg = "";
		
		out.println("<h1> Migration Completed... </h1>");
		out.println("<h1>................................................................................................................. </h1>");
		
		Data obj = new Data();  // Master Entry
	
		String returnState = obj.insert_State(); // this method migrate the state records.
		out.println("<h3>" + returnState + "</h3>");
	   
		
		
		String returnCity = obj.insert_City(); // this method migrate the city records.
		out.println("<h3>" + returnCity + "</h3>");
		
		String returnRelation = obj.insert_CustomerRelation(); // this methods migrate the CustomerRelation records.
		out.println("<h3>" + returnRelation + "</h3>");
		
		String returnStu = obj.insert_StudentRelation(); // this methods migrate the StidentRelation records.
		out.println("<h3>" + returnStu + "</h3>");
		
		//------------------------------------------------------------------------------------
		
		SchoolMigrate objSchool = new SchoolMigrate();     // SchoolMigrate Class 
		String returnSchool = objSchool.insert_SchoolInfo();
		out.println("<h3>" + returnSchool + "</h3>");
		
		String returnClass = objSchool.insert_ClassroomInfo();
		out.println("<h3>" + returnClass + "</h3>");
		
		String returnEmail = objSchool.insert_Email_Setting();
		out.println("<h3>" + returnEmail + "</h3>");
		
		String returnCamera = objSchool.insert_Camera_Details();
		out.println("<h3>" + returnCamera + "</h3>");
		
		//------------------------------------------------------------------------------------
		
		InfantUnitMesure objUnit = new  InfantUnitMesure();
		String returnUnit = objUnit.insert_Unit_Measure();
		out.println("<h3>" + returnUnit + "</h3>");	
		
		String returnUValue = objUnit.insert_Unit_Value();
		out.println("<h3>" + returnUValue + "</h3>");	
		
		String returnFood = objUnit.insert_Food();
		out.println("<h3>" + returnFood + "</h3>");
		
		ParentMigrate objParent = new ParentMigrate();  // Parent Object
		String returnParent = objParent.insert_Parent_details();  // This method migrate the Parent details,Email details, Contact details.
		out.println("<h3>" + returnParent + "</h3>");
		
		InfantMigrate objInfant = new InfantMigrate();  // Infant Object.
	    String returnInfant = objInfant.infant_Details_Insert();  // This method migrate the infant details and Allergies.
	    out.println("<h3>" + returnInfant + "</h3>");
		
		String StuCheckIn = objUnit.inset_Student_Check_In();
		out.println("<h3>" + StuCheckIn + "</h3>");
		
		String StuCheckOut = objUnit.insert_Student_CheckOut();
		out.println("<h3>" + StuCheckOut + "</h3>");
		
		String StuIfnExFood = objUnit.insert_Infant_ExpectedFood();
		out.println("<h3>" + StuIfnExFood + "</h3>");
		
		String StugnGvFood = objUnit.insert_Infant_Given_Food();
		out.println("<h3>" + StugnGvFood + "</h3>");
		
		String StuMed = objUnit.insert_Infant_Medication();
		out.println("<h3>" + StuMed + "</h3>");
		
		String StuExMed = objUnit.insert_Infant_Expected_Medication();
		out.println("<h3>" + StuExMed + "</h3>");
		
		String StuGvMed = objUnit.infant_Infant_Given_Medication();
		out.println("<h3>" + StuGvMed + "</h3>");
		
		String StuCkMed = objUnit.insert_Student_CheckIn_Infant_Medication();
		out.println("<h3>" + StuCkMed + "</h3>");
		
		
		//-------------------------------------------------------------------------------------
		
		InfantNap objNap = new InfantNap();
		
		msg = objNap.insert_Infant_Expected_Nap();
		out.println("<h3>" + msg + "</h3>");
		
		msg= objNap.insert_Infant_Nap();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Infant_Diaper();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Infant_Activity();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Student_CheckOut_Infant_Activity();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Student_Action_Constraint_Type();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Student_Action_Type();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Student_Action();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Student_Action_Constraint();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Active_Student_Action_Constraint();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Student_Action_Contstraint_Age_Block();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Constrained_Student_Actions();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Student_Action_Unblock();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Student_Action_Color();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Student_Log();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objNap.insert_Student_Log_Student_Action();
		out.println("<h3>" + msg + "</h3>");
		//-------------------------------------------------------------------------------------
		
		StudentProgram objStu=new StudentProgram();
		
		msg = objStu.insert_Student_Program();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objStu.insert_Enrollment_Change_Type();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objStu.insert_Enrollment_End_Status();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objStu.insert_Enrollment();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objStu.insert_Student_Bak();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objStu.insert_StudentRoll();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objStu.insert_Student_Absent_Week();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objStu.insert_Student_Pickup_Authorized();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objStu.insert_Special_Condition();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objStu.insert_Student_Special_Condition();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objStu.insert_Student_Action_Graph();
		out.println("<h3>" + msg + "</h3>");
		
		//-------------------------------------------------------------------------------------
		
		ParentQuestion objPrt=new ParentQuestion();
		
		msg = objPrt.insert_Question_Type();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objPrt.insert_Question();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objPrt.insert_Answer();
		out.println("<h3>" + msg + "</h3>");
		
		//-------------------------------------------------------------------------------------
		
		TaskDept objTask=new TaskDept();
		
		msg = objTask.insert_Task_Dept();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objTask.insert_Task_Type();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objTask.insert_Task_Status();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objTask.insert_Task();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objTask.insert_Task_User_Settings();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objTask.insert_Task_ReadBy();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objTask.insert_Task_Resource();
		out.println("<h3>" + msg + "</h3>");
		
		//-------------------------------------------------------------------------------------
		
		IndividualTables objInd=new IndividualTables();
		
		msg = objInd.insert_Audit();
		out.println("<h3>" + msg + "</h3>");
		
		msg = objInd.insert_Audit_Values();
		out.println("<h3>" + msg + "</h3>");
		
	    msg = objInd.insert_Date_Lookup();
	    out.println("<h3>" + msg + "</h3>");
	    
	    msg = objInd.insert_Computer_Location();
	    out.println("<h3>" + msg + "</h3>");
	    
	    msg = objInd.insert_Note_Type();
	    out.println("<h3>" + msg + "</h3>");
	    
	    msg = objInd.insert_Note();
	    out.println("<h3>" + msg + "</h3>");
	    
	    msg = objInd.insert_Note_Setting();
	    out.println("<h3>" + msg + "</h3>");
	    
	    msg = objInd.insert_Child_Session();
	    out.println("<h3>" + msg + "</h3>");
	    
	    msg = objInd.insert_Customer_Email_Statement();
	    out.println("<h3>" + msg + "</h3>");
	    
	    msg = objInd.insert_Classroom_Progression();
	    out.println("<h3>" + msg + "</h3>");
	    
	    msg = objInd.insert_Area();
	    out.println("<h3>" + msg + "</h3>");
	    
	    msg = objInd.insert_Numbers();
	    out.println("<h3>" + msg + "</h3>");
	    
	}

}
