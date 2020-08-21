package Main;

import java.sql.Statement;

public class DeleteTableValues {
	
	Dabase_Connect conn = new Dabase_Connect();
	String msg="";
	
	public String deleteTable()
	{
		try
		{
			Statement stMySql = conn.connectionmysql().createStatement();
			Logs.writeLogTest("Step2");
			String u1="update id_manager set Max_Id=0 ";
			stMySql.executeUpdate(u1);
			
			//----------------------------
			
			String inftRlt="truncate table parent_infant_relation";
			stMySql.executeUpdate(inftRlt);
						
			String num="truncate table numbers";
			stMySql.executeUpdate(num);
						
			String Area="truncate table area";
			stMySql.executeUpdate(Area);
						
			String Clsprog="truncate table classroom_progression";
			stMySql.executeUpdate(Clsprog);
						
			String CustEmlSt="truncate table customer_email_statement";
			stMySql.executeUpdate(CustEmlSt);
						
			String ChldSeion="truncate table infant_live";
			stMySql.executeUpdate(ChldSeion);
			
			String NtStg="truncate table note_setting";
			stMySql.executeUpdate(NtStg);
			
			String Nt="truncate table note";
			stMySql.executeUpdate(Nt);
			
			String NtTpe="truncate table note_type";
			stMySql.executeUpdate(NtTpe);
			
			String ComLo="truncate table computer_location";
			stMySql.executeUpdate(ComLo);
			
			String DtLo="truncate table date_lookup";
			stMySql.executeUpdate(DtLo);
			
			String AuditVal="truncate table audit_values";
			stMySql.executeUpdate(AuditVal);
			
			String Audit="truncate table audits";
			stMySql.executeUpdate(Audit);
			
			String taskRsoce="truncate table task_resource";
			stMySql.executeUpdate(taskRsoce);
			
			String taskRdBy="truncate table task_read_by";
			stMySql.executeUpdate(taskRdBy);
			
			String taskUsrStg="truncate table task_user_settings";
			stMySql.executeUpdate(taskUsrStg);
			
			String task="truncate table task";
			stMySql.executeUpdate(task);
			
			String taskSts="truncate table task_status";
			stMySql.executeUpdate(taskSts);
			
			String taskDptType="truncate table task_type";
			stMySql.executeUpdate(taskDptType);
			
			String taskDpt="truncate table task_dept";
			stMySql.executeUpdate(taskDpt);
			
			String PrtAns="truncate table answer";
			stMySql.executeUpdate(PrtAns);
			
			String PrtQs=" truncate table question";
			stMySql.executeUpdate(PrtQs);
			
			String PrtQust=" truncate table question_type ";
			stMySql.executeUpdate(PrtQust);
			
			String StuActGrp="truncate table student_action_graph ";
			stMySql.executeUpdate(StuActGrp);
			
			String StuSpcCond="truncate table student_special_condition ";
			stMySql.executeUpdate(StuSpcCond);
			
			String SpcCond="truncate table special_condition ";
			stMySql.executeUpdate(SpcCond);
			
			String StuPick="truncate table student_pickup_authorized ";
			stMySql.executeUpdate(StuPick);
			
			
			String StuAbsBak="truncate table Student_Absent_Week ";
			stMySql.executeUpdate(StuAbsBak);
								
			String StuAttDets=" truncate table infant_attendance ";
			stMySql.executeUpdate(StuAttDets);
			
			
			String StuAttds=" truncate table attendance ";
			stMySql.executeUpdate(StuAttds);
			
			
			String StuBreakFast=" truncate table infant_breakfast_plan ";
			stMySql.executeUpdate(StuBreakFast);
			
			String StuInfoDetails=" truncate table infant_additional_info ";
			stMySql.executeUpdate(StuInfoDetails);
			
			String StuBak=" truncate table infant_course_details ";
			stMySql.executeUpdate(StuBak);
			
			String StuEnrmt=" truncate table enrollment ";
			stMySql.executeUpdate(StuEnrmt);
			
			String StuEnrEnd=" truncate table enrollment_end_status ";
			stMySql.executeUpdate(StuEnrEnd);
						
			String StuEnr=" truncate table enrollment_change_type ";
			stMySql.executeUpdate(StuEnr);
			
			String StuProg=" truncate table program ";
			stMySql.executeUpdate(StuProg);		
			
			String StuLogAct="truncate table student_log_student_action ";
			stMySql.executeUpdate(StuLogAct);
			
			String StuLog="truncate table student_log ";
			stMySql.executeUpdate(StuLog);
						
			String StuColr="truncate table student_action_color";
			stMySql.executeUpdate(StuColr);
			
			String AgeBlk="truncate table student_action_unblock ";
			stMySql.executeUpdate(AgeBlk);
			
			String ConsStu="truncate table constrained_student_actions ";
			stMySql.executeUpdate(ConsStu);
			
			
			
			///////////////////////////////////////////////
			Logs.writeLogTest("44");
			String StuAge="truncate table student_action_contstraint_age_block";
			stMySql.executeUpdate(StuAge);
			
			Logs.writeLogTest("Step3");
			String ActStuCons="truncate table active_student_action_constraint";
			stMySql.executeUpdate(ActStuCons);
			
			Logs.writeLogTest("Step3");
			String StuActCons="truncate table student_action_constraint";
			stMySql.executeUpdate(StuActCons);
			
			Logs.writeLogTest("Step3");
			String StuActs="truncate table student_action";
			stMySql.executeUpdate(StuActs);
			
			Logs.writeLogTest("Step3");
			String StuAct="truncate table student_action_type";
			stMySql.executeUpdate(StuAct);
			
			Logs.writeLogTest("Step3");
			String StuCons="truncate table student_action_constraint_type ";
			stMySql.executeUpdate(StuCons);
			
			Logs.writeLogTest("Step3");
			String Studel="truncate table student_check_out_infant_activity";
			stMySql.executeUpdate(Studel);
			
			Logs.writeLogTest("55");
			String infAct="truncate table infant_activity";
			stMySql.executeUpdate(infAct);
			
			Logs.writeLogTest("Step3");
			String infDip="truncate table infant_diaper";
			stMySql.executeUpdate(infDip);
			
			Logs.writeLogTest("66");
			String infNapD="truncate table infant_nap ";
			stMySql.executeUpdate(infNapD);
			
			Logs.writeLogTest("Step3");
			String infNap="truncate table infant_expected_nap";
			stMySql.executeUpdate(infNap);
			
			Logs.writeLogTest("Step3");
			//-----------------------------
			
			String qStuCkMed="truncate table student_checkin_infant_medication";
			stMySql.executeUpdate(qStuCkMed);
			
			Logs.writeLogTest("Step3");
			String qStuGvMed="truncate table infant_given_medication";
			stMySql.executeUpdate(qStuGvMed);
			
			Logs.writeLogTest("Step3");
			String qStuExMed="truncate table infant_expected_medication";
			stMySql.executeUpdate(qStuExMed);
			
			Logs.writeLogTest("77");
			String qStuMed="truncate table  infant_medication";
			stMySql.executeUpdate(qStuMed);
			
			Logs.writeLogTest("78");
			String qIfnGvFood="truncate table infant_given_food";
			stMySql.executeUpdate(qIfnGvFood);
			
			Logs.writeLogTest("79");
			String qIfnExFood="truncate table infant_expected_food";
			stMySql.executeUpdate(qIfnExFood);
			
			Logs.writeLogTest("80");
			String qStuNotes="truncate table  infant_notes";
			stMySql.executeUpdate(qStuNotes);
			
			Logs.writeLogTest("81");
			String qStuCheckOut="truncate table studentcheckout";
			stMySql.executeUpdate(qStuCheckOut);
			
			Logs.writeLogTest("82");
			String qStuCheckIn="truncate table  studentcheckin";
			stMySql.executeUpdate(qStuCheckIn);
			
			Logs.writeLogTest("83");
			String q1=" truncate table infant_allergies";
			stMySql.executeUpdate(q1); 
			
					
			stMySql.executeUpdate("truncate table infant_activities_details"); 
			
			stMySql.executeUpdate("truncate table supplies_needed"); 
			
			
			
			Logs.writeLogTest("84");
			String q2=" truncate table infant_account_details ";
			stMySql.executeUpdate(q2);
			
			Logs.writeLogTest("85"); 
			String qFood="truncate table infantfood";
			stMySql.executeUpdate(qFood);
			
			Logs.writeLogTest("86");
			String qValue="truncate table unitofmeasurevalue";
			stMySql.executeUpdate(qValue);
			
			Logs.writeLogTest("87");
			String qUnit="truncate table unitofmeasure";
			stMySql.executeUpdate(qUnit);
			
			Logs.writeLogTest("88");
			//----------------------------
			
			String q3="truncate table parent_contact_details";
			stMySql.executeUpdate(q3);
			
			Logs.writeLogTest("89");
			String q4="truncate table parent_email_details";
			stMySql.executeUpdate(q4);
			
			Logs.writeLogTest("90");
			String q5="truncate table parent_account_details";
			stMySql.executeUpdate(q5);
			
			
			Logs.writeLogTest("91");
			String qCamere="truncate table camera_details";
			stMySql.executeUpdate(qCamere);
			
			Logs.writeLogTest("92");
			String q9="truncate table classroom";
			stMySql.executeUpdate(q9);
			
			Logs.writeLogTest("93");
			String q12="truncate table emailsetting";
			stMySql.executeUpdate(q12);
			
			Logs.writeLogTest("94");
			String q11="truncate table school_contact_details";
			stMySql.executeUpdate(q11);
			
			// New Added truncation table.
			Logs.writeLogTest("95.1");
			stMySql.executeUpdate("truncate table crib_details");
						
			Logs.writeLogTest("95.2");
			stMySql.executeUpdate("truncate table fee_details");
			
			Logs.writeLogTest("95.3");
			stMySql.executeUpdate("truncate table school_email_details");
			//========================================
			
			Logs.writeLogTest("95");
			String q10="truncate table school_info";
			stMySql.executeUpdate(q10);
			
			Logs.writeLogTest("96");
			String q6="truncate table city";
			stMySql.executeUpdate(q6);
			
			Logs.writeLogTest("97");
			String q7="truncate table state";
			stMySql.executeUpdate(q7);
			
			Logs.writeLogTest("98");
			String q8="truncate table relations";
			stMySql.executeUpdate(q8);
			
			Logs.writeLogTest("00");
			msg="All records are successfully reset ";
			
			stMySql.close();
			
		}
		catch(Exception ex)
		{
			Logs.writeLogTest(ex.getMessage().toString());
			//Logs.writeLog(ex.getMessage().toString(),"Delete_Table");
			ex.printStackTrace();
		}
		return msg;
	}
	

}
