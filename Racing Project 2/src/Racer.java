public class Racer {
	
		private long startTime, finishTime;
		private int number;
		private boolean DNF; 
		private boolean started, finished;

		public Racer (int number)
		{
			this.number=number;
			startTime=-1;
			finishTime=-1;
			DNF=false;
			started=false;
			finished=false;
		}
		public void setStart (long start)
		{
			startTime=start;
			started= true;
		}
		public void setFinish (long finish)
		{
			finishTime=finish;
			finished=true;
		}
		public void setDNF (boolean dnf)
		{
			DNF=dnf;
		}
		public long getStar()
		{
			return startTime;
		}
		public long getFinish()
		{
			return finishTime;
		}
		public long getRunTime()
		{
			return finishTime-startTime;
		}
		public boolean started()
		{
			
			return started;
		}
		public boolean finished()
		{
			return finished;
		}
		public boolean DNF()
		{
			return DNF;
		}

		
}
