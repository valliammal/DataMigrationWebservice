package Main;

public class GlobalValues {

		public static final int rowIncrement=200;
		
		public static Object date_format(Object Date_Time)
		{
			if( Date_Time != null &&  !Date_Time.equals("") )
			 {
				return " str_to_date('" +Date_Time+ "','%Y-%m-%d')";
			 }
			else
			{
				return null;
			}
		}
		
		// Conversion date format with timing
		public static Object date_format_time(Object Date_Time)
		{
			if( Date_Time != null &&  !Date_Time.equals("") )
			 {
				return " str_to_date('" +Date_Time+ "','%Y-%m-%d %H:%i:%s')";
			 }
			else
			{
				return null;
			}
		}
}
