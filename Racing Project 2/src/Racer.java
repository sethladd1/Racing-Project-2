public class Racer {
	
		private long s, f;
		private boolean dnf; 

		public int Racers (int number)
		{
		
			return number;
		}
		public void setStart (long start)
		{
			s=start;
		}
		public void setFinish (long finish)
		{
			f=finish;
		}
		public void setDNF (boolean a)
		{
			dnf=a;
		}
		public long getStar()
		{
			return s;
		}
		public long getFinish()
		{
			return f;
		}
		public long getRunTime()
		{
			return f-s;
		}
		public boolean started()
		{
			return true;
		}
		public boolean finished()
		{
			return true;
		}
		public boolean DNF()
		{
			return true;
		}
		
}
