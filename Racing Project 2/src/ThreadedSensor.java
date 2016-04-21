import java.awt.event.*;
import javax.swing.*;

public class ThreadedSensor extends Thread{
	int i;
	Shell s;
	JFrame jf;
	ThreadedSensor(Shell s, int i){this.i=i;this.s=s;}
	public void run(){
		try{
			ThreadedSensor t=this;
			jf=new JFrame();
			JButton jb=new JButton("Sensor "+i);
			ActionListener l = new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent arg0) {
				//s.getCurrentRun().trigger(i);
					if(i==7){s.interruptThreads();}
					t.interrupt();
					System.out.println(this.toString());
					jf.dispose();
				}
			};
			jb.addActionListener(l);
			jf.add(jb);
			jf.setSize(300, 100);
			jf.setVisible(true);
			//while(true){sleep(5000);}
		}catch(Exception e){System.out.println(this.toString());}
	}
}
