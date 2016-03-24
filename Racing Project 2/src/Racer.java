

public class Racer {
	
		private long s=-1, f=-1;
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
			if (s>=0)
				return true;
			else
				return false;
		}
		public boolean finished()
		{
			if (f>=0)
				return true;
			else
				return false;
		}
		public boolean DNF()
		{
			return dnf;
		}
		
}
