import javax.swing.*;
import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonAreaLayout;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Scanner;
public class GUI extends JFrame{
	final static ImageIcon enabled = new ImageIcon("Icons/enabledChan.png");
	final static ImageIcon disabled = new ImageIcon("Icons/disabledChan.png");
	//	final static ImageIcon leftArrow = new ImageIcon("Icons/leftArrow.png");
	//	final static ImageIcon rightArrow = new ImageIcon("Icons/rightArrow.png");
	//	final static ImageIcon upArrow = new ImageIcon("Icons/upArrow.png");
	//	final static ImageIcon downArrow = new ImageIcon("Icons/downArrow.png");
	private ArrayList<JLabel> channels;
	private ArrayList<JButton> triggers;
	private ArrayList<JButton> numPad;
	private ArrayList<JComboBox<String>> sensors;
	private JTextArea display;
	private JTextArea printer;
	private JButton swapButton, powerButton, commandsButton, printPowerButton;
	//	private JLabel left, right, down, up;
	private Timer t;
	private boolean commandMode, printPower;
	private Shell shell;
	//	XXX: as user presses number buttons append the number to input; read input when '#' is pressed; 
	private String input;
	private int cmd;
	private ArrayList<String> printerText;
	public GUI(Shell s){
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		shell = s;
		input ="";
		cmd=0;
		channels = new ArrayList<JLabel>();
		triggers = new ArrayList<JButton>();
		sensors = new ArrayList<JComboBox<String>>();
		display = new JTextArea();
		display.setEditable(false);
		display.setBackground(Color.BLACK);
		display.setForeground(Color.WHITE);
		
		display.setFont(new Font(display.getFont().getFontName(), Font.BOLD, 11));
		numPad = new ArrayList<JButton>();
		swapButton = new JButton("Swap");
		swapButton.addActionListener(new ButtonActions());
		commandsButton = new JButton("Commands");
		commandsButton.addActionListener(new ButtonActions());
		powerButton = new JButton("Power");
		powerButton.addActionListener(new ButtonActions());
		printPowerButton = new JButton("Printer Power");
		printPowerButton.addActionListener(new ButtonActions());
		printer = new JTextArea();
		printer.setEditable(false);
		BorderLayout bl = new BorderLayout(10, 10);
		setLayout(bl);
		//		left = new JLabel(leftArrow);
		//		right = new JLabel(rightArrow);
		//		up = new JLabel(upArrow);
		//		down = new JLabel(downArrow);

		JLabel l;
		JButton b;

		for(int i=0; i<8; ++i){
			l = new JLabel(disabled);
			l.addMouseListener(new ClickListener());
			b = new JButton();
			b.setBackground(Color.BLUE);
			b.addActionListener(new TriggerListener());
			channels.add(l);
			triggers.add(b);
		}

		for(int i = 0; i<12;++i){

			if(i==9){
				b = new JButton("*");

			}
			else if(i==11){
				b = new JButton("#");
			}
			else if(i==10){
				b = new JButton("0");
			}
			else{
				b = new JButton(String.valueOf(i+1));
			}
			b.setFont(new Font(b.getFont().getFontName(), b.getFont().getStyle(), 18));
			b.addActionListener(new ButtonActions());
			numPad.add(b);
		}
		JComboBox<String> cb;
		for(int i=0;i<8;++i){
			cb = new JComboBox<String>();
			cb.addItem("");
			cb.addItem("EYE");
			cb.addItem("GATE");
			cb.addItem("PAD");
			cb.addActionListener(new SensorListener());
			sensors.add(cb);
		}
		t = new Timer(20, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateDisplay();
			}
		});
		t.stop();
		t.setRepeats(true);
		setUpUI();

	}

	private void setUpUI(){
//		Center Panel
		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		JPanel trigChanGrid = new JPanel();
		GridLayout gridLayout =new GridLayout(6,5);
		gridLayout.setHgap(5);
		gridLayout.setVgap(3);

		trigChanGrid.setLayout(gridLayout);
		JPanel textLabels = new JPanel();
		textLabels.setLayout(new BoxLayout(textLabels, BoxLayout.Y_AXIS));
		JLabel title = new JLabel("ChronoTimer 1009");

		JLabel l;
		JButton b;

		trigChanGrid.add(new JLabel());
		for(int i=1; i<8; i+=2){
			l = new JLabel(Integer.toString(i));
			l.setHorizontalAlignment(JLabel.CENTER);
			l.setVerticalAlignment(JLabel.BOTTOM);
			trigChanGrid.add(l);
		}
		l = new JLabel("Start");
		l.setHorizontalAlignment(JLabel.RIGHT);
		trigChanGrid.add(l);
		for(int i=0; i<7; i+=2){
			trigChanGrid.add(this.triggers.get(i));
		}
		l = new JLabel("Arm/Disarm");
		l.setHorizontalAlignment(JLabel.RIGHT);
		trigChanGrid.add(l);
		for(int i=0; i<7; i+=2){
			trigChanGrid.add(channels.get(i));
		}
		trigChanGrid.add(new JLabel());
		for(int i=2; i<9; i+=2){
			l = new JLabel(Integer.toString(i));
			l.setHorizontalAlignment(JLabel.CENTER);
			l.setVerticalAlignment(JLabel.BOTTOM);
			trigChanGrid.add(l);
		}
		l = new JLabel("Finish");
		l.setHorizontalAlignment(JLabel.RIGHT);
		trigChanGrid.add(l);
		for(int i=1; i<8; i+=2){
			trigChanGrid.add(triggers.get(i));
		}
		l = new JLabel("Arm/Disarm");
		l.setHorizontalAlignment(JLabel.RIGHT);
		trigChanGrid.add(l);

		for(int i=1; i<8; i+=2){
			trigChanGrid.add(channels.get(i));
		}

		add(title, BorderLayout.NORTH);
		Font f = title.getFont();
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(new Font(f.getName(), f.getStyle(), 16));
		center.add(trigChanGrid);
		center.add(new JLabel(" "));
		center.add(display);
		add(center, BorderLayout.CENTER);

		JPanel west = new JPanel();
		west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(powerButton);
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(commandsButton);
		add(west, BorderLayout.WEST);



		JPanel east = new JPanel();
		east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
		east.add(new JLabel(" "));
		east.add(new JLabel(" "));
		east.add(new JLabel(" "));

		east.add(printPowerButton);
		east.add(new JLabel(" "));
		east.add(printer);
		east.add(new JLabel(" "));
		east.add(new JLabel(" "));
		east.add(new JLabel(" "));
		JPanel numPan = new JPanel();
		numPan.setLayout(new GridLayout(4, 4));
		for(JButton btn : numPad){
			numPan.add(btn);

		}
		east.add(numPan);
		east.add(new JLabel(" "));



		add(east, BorderLayout.EAST);
		numPan.setPreferredSize(new Dimension(numPan.getPreferredSize().width, numPan.getPreferredSize().height+50));
		east.setPreferredSize(new Dimension(east.getPreferredSize().width+50, east.getPreferredSize().height));

		//		South Panel
		JPanel south = new JPanel();

		south.setLayout(new BoxLayout(south, BoxLayout.X_AXIS));
		JPanel sensorGrid = new JPanel(new GridLayout(4, 5));

		for(int i=0;i<4;++i){
			for(int j=0;j<5;++j){
				if(j==0&&i%2==0){
					l = new JLabel("Channel");
					l.setHorizontalAlignment(JLabel.CENTER);
					//					l.setVerticalAlignment(JLabel.BOTTOM);
					sensorGrid.add(l);
				}
				else{ 
					if(i%2==0){
						l = new JLabel(String.valueOf(j+2*i));
						l.setHorizontalAlignment(JLabel.CENTER);
						//						l.setVerticalAlignment(JLabel.BOTTOM);
						sensorGrid.add(l);
					}
					else
						if(j==0){
							l = new JLabel("Sensor");
							l.setHorizontalAlignment(JLabel.CENTER);
							//							l.setVerticalAlignment(JLabel.BOTTOM);
							sensorGrid.add(l);
						}
						else	{
							if(i==1){
								sensorGrid.add(sensors.get(j-1));
							}
							else{
								sensorGrid.add(sensors.get(j+3));
							}
						}
				}

			}
		}


		south.add(sensorGrid);
		add(south,BorderLayout.SOUTH);

		setSize(820, 700);
		

	}
	/**
	 * called by shell if a channel is toggled by cmdline to ensure it shows in the GUI
	 */
	public void syncChanIcons(){
		Run curRun = shell.getCurrentRun();
		for(int i=1;i<9; i++){
			if(curRun.getChannel(i)){
				channels.get(i-1).setIcon(enabled);
			}
			else{
				channels.get(i-1).setIcon(disabled);
			}
		}
	}
	public void reset(){
		shell.readCommand("RESET");
		for(JLabel l : channels){
			l.setIcon(disabled);
		}
		
	}
	private void updateDisplay(){
		Run curRun = shell.getCurrentRun();

		display.setText("Time: "+curRun.getElapsedTime() +"\n");

		ArrayList<Racer> rcrs;
		switch(curRun.getType()){
		case 0:
			rcrs = curRun.getFinishQueue1();
			for(Racer r : rcrs){
				display.append(r.getNumber()+": "+curRun.getRunningTime(r)+" R\n");
			}
			rcrs = curRun.getStartQueue1();
			for(int i=0 ; i<3 && i<rcrs.size();++i){
				display.append(rcrs.get(i).getNumber()+": "+" Q\n");
			}
			Racer r = curRun.getLastFinish1();
			if(r!=null)
				display.append(r.getNumber()+": "+curRun.getRunningTime(r)+" F");
			break;
		case 1:
			rcrs = curRun.getFinishQueue1();
			ArrayList<Racer> rc2 = curRun.getFinishQueue2();
			for(int i=0;i<Math.max(rc2.size(), rcrs.size());++i){
				if(i<rcrs.size())
					display.append(rcrs.get(i).getNumber()+": "+curRun.getRunningTime(rcrs.get(i))+" R   ");
				if(i<rc2.size())
					display.append(rc2.get(i).getNumber()+": "+curRun.getRunningTime(rc2.get(i))+" R\n");
				else
					display.append("\n");
			}
			rcrs = curRun.getStartQueue1();
			rc2 = curRun.getStartQueue2();
			if(!rcrs.isEmpty())	
				display.append(rcrs.get(0).getNumber()+": "+curRun.getRunningTime(rcrs.get(0))+" Q   ");
			if(!rc2.isEmpty())
				display.append(rc2.get(0).getNumber()+": "+curRun.getRunningTime(rc2.get(0))+" Q\n");
			else
				display.append("\n");
			Racer r1 = curRun.getLastFinish1();
			Racer r2 = curRun.getLastFinish2();
			
			if(r1!=null)
				display.append(r1.getNumber()+": "+curRun.getRunningTime(r1)+" F   ");
			if(r1!=null)
				display.append(r2.getNumber()+": "+curRun.getRunningTime(r2)+" F");
			
			break;
		case 2:
			ArrayList<Long> ranks = curRun.getGroupRanks();
			if(!ranks.isEmpty()){
				String rank = String.valueOf(ranks.size());
				while(rank.length()<5)
					rank = "0"+rank;
				display.append(rank + " " +Time.convertToTimestamp(ranks.get(ranks.size()-1))+"\n");
				
			}
			break;
		}
	}
	public void print(String str){
		if(!printPower) return;
		Scanner sc = new Scanner(str);
		FontMetrics fm = printer.getFontMetrics(printer.getFont());
		while(sc.hasNextLine()){
			try{
				if((printer.getLineCount()+1)*fm.getHeight()>=printer.getHeight()){
					System.out.println(45);
					int offset = printer.getLineStartOffset(1);
					String txt = printer.getText();

					printer.setText(txt.substring(offset, txt.length()));
				}
			}catch(Exception es){
				System.out.println(es.toString());
			}
			String s = sc.nextLine()+"\n";
			
			if(fm.stringWidth(s)>printer.getWidth()){
				String arr[]=s.split("\\h");
				s="";
				for(int i=0;i<arr.length;++i){
					if(fm.stringWidth(s+arr[i])>printer.getWidth()){
						printer.append(s+"\n");
						s="";
					}
					else{
						s+=arr[i]+" ";
					}
				}
				
			}
			printer.append(s+"\n");
		}
	}
	private void commands(int command){
		String str="";
		switch(command){
		case 0:
			str = "1. Event Type\n"+"2. Time\n"+"3. Add Racer\n" + "4. Clear Racer\n" + "5. End Run\n" + "6. New Run\n" + "7. Reset\n" + "8. Print\n9.Export\n";
			if(shell.getCurrentRun().getType()==0){
				str += "10. Did Not Finish\n";
			}
			str += "\n";
			display.setText(str);
			cmd=0;
			break;
		case 1:
			str = "1. IND\n"+"2. PARIND\n"+"3.GRP\n\n";
			cmd=1;
			input="";
			break;
		case 2:
			str="Enter <hr>*<min>*<sec>: ";
			input="";
			cmd=2;
			break;
		case 3:
			str="Enter Number: ";
			input="";
			cmd=3;
			break;
		case 4:
			str="Enter Number: ";
			input="";
			cmd=4;
			break;
		case 5:
			if(!shell.readCommand("ENDRUN")){
				print(shell.getErrorMessage());
			}
			input="";
			cmd=0;
			commandMode=false;
			t.start();
			break;
		case 6:
			if(!shell.readCommand("NEWRUN")){
				print(shell.getErrorMessage());
			}
			input="";
			cmd=0;
			commandMode=false;
			t.start();
			break;
		case 7:
			reset();
			input="";
			cmd=0;
			commandMode=false;
			t.start();
			break;
		case 8:
			str ="Enter Run Number: ";
			cmd=8;
			input="";
			break;
		case 9:
//			TODO exprt
			str ="Enter Run Number: ";
			cmd=9;
			input="";
			break;
		case 10:
			shell.readCommand("DNF");
			input="";
			cmd=0;
			commandMode=false;
			t.start();
			break;
		default:
			str="Invalid Input";
			Timer t2 = new Timer(1000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					input = "";
					commands(cmd);
				}
			});
			t2.setRepeats(false);
			t2.start();
		}
		display.setText(str);

	}
	private void readCommand(){
		boolean badInput = false;
		switch(cmd){
		case 0:
			commands(Integer.parseInt(input));
			break;
		case 1:
			if(input.equals("1"))
				shell.readCommand("EVENT IND");
			else if(input.equals("2"))
				shell.readCommand("EVENT PARIND");
			else if(input.equals("3"))
				shell.readCommand("EVENT GRP");
			else
				badInput=true;
			break;
		case 2:
			String arr[] = input.split("\\*");
			if(arr.length<3) {
				badInput=true;
			}
			else{
				if(!shell.readCommand("TIME "+arr[0]+":"+arr[1]+":"+arr[2]))
					print(shell.getErrorMessage());

			}
			
			break;
		case 3:
			if(!shell.readCommand("NUM " + input))
				print(shell.getErrorMessage());
			
			break;
		case 4:
			if(!shell.readCommand("CLR " + input))
				print(shell.getErrorMessage());
			break;
		case 8:
			if(!shell.readCommand("PRINT " + input))
				print(shell.getErrorMessage());
			break;
		case 9: 
			if(!shell.readCommand("EXPORT " + input))
				print(shell.getErrorMessage());
			break;
		}
		if(badInput){
			display.setText("Invalid Input");
			Timer t2 = new Timer(1000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					input = "";
					commands(cmd);
				}
			});
			t2.setRepeats(false);
			t2.start();
		}
		else if(cmd!=0){
			input = "";
			commandMode=false;
			t.start();
		}
	}

	private class ClickListener extends MouseAdapter{
		public void mousePressed(MouseEvent e){
			JLabel l = (JLabel)e.getSource();
			ImageIcon icon = (ImageIcon) l.getIcon();
			int minX = l.getWidth()/2 - icon.getIconWidth()/2;
			int maxX = l.getWidth()/2 + icon.getIconWidth()/2;
			int minY = l.getHeight()/2 - icon.getIconHeight()/2;
			int maxY = l.getHeight()/2 + icon.getIconHeight()/2;

			int centerX;
			int centerY; 
			int x, y;
			if(icon==disabled || icon == enabled){
				int chan =  channels.indexOf(l)+1;
				if(shell.readCommand("TOG "+chan)){
					int rad = icon.getIconWidth()/2;
					centerX = l.getWidth()/2;
					centerY = l.getHeight()/2;
					y = Math.abs(e.getY()-centerY);
					x = Math.abs(e.getX()-centerX);

					if(y*y+x*x<=rad*rad){
						if(icon == disabled){
							l.setIcon(enabled);
						}
						else{
							l.setIcon(disabled);
						}
					}
				}
				else{
					if(printPower){
						printer.append(shell.getErrorMessage());
					}
				}
			}


		}
	}

	private class TriggerListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton)e.getSource();
			int chan = triggers.indexOf(btn)+1;
			
			if(!shell.readCommand("TRIG " + chan))
				print(shell.getErrorMessage());

		}

	}
	private class ButtonActions implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource();
			if(btn== powerButton){
				shell.readCommand("POWER");
				return;
			}
			if(shell.getPower()){
				switch(btn.getText()){
				case "Printer Power":
					printPower = !printPower;
					if(printPower)
						print("Printer on");
					break;
				case "Commands":
					if(!commandMode){
						t.stop();
						commandMode = true;
						commands(0);
					}
					else{
						t.start();
						commandMode = false;
					}
					break;
				case "Swap":
					shell.readCommand("SWAP");
					if(!shell.getErrorMessage().isEmpty()){
						print(shell.getErrorMessage());
					}
					break;
				case "1":
					if(commandMode){
						input+="1";
						display.append("1");
					}
					break;
				case "2":
					if(commandMode){
						input+="2";
						display.append("2");
					}
					break;
				case "3":
					if(commandMode){
						input+="3";
						display.append("3");
					}
					break;
				case "4":
					if(commandMode){
						input+="4";
						display.append("4");
					}
					break;
				case "5":
					if(commandMode){
						input+="5";
						display.append("5");
					}
					break;
				case "6":
					if(commandMode){
						input+="6";
						display.append("6");
					}
					break;
				case "7":
					if(commandMode){
						input+="7";
						display.append("7");
					}
					break;
				case "8":
					if(commandMode){
						input+="8";
						display.append("8");
					}
					break;
				case "9":
					if(commandMode){
						input+="9";
						display.append("9");
					}
					break;
				case "0":
					if(commandMode){
						input+="0";
						display.append("0");
					}
					break;
				case "*":
					if(commandMode && cmd==2){
						input+="*";
						display.append("*");
					}
					break;
				case "#":
					if(commandMode){
						if(cmd==0){
							commands(Integer.parseInt(input));
						}
						else
							readCommand();
					}
					break;
				}
			}
		}

	}
private class SensorListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox<String> cb = (JComboBox<String>)e.getSource();
		String s = (String)cb.getSelectedItem();
		if(s.isEmpty()){
			shell.readCommand("DISC "+(sensors.indexOf(cb)+1));
		}
		else{
			shell.readCommand("CONN " + s+" "+ (sensors.indexOf(cb)+1));
		}
	}
	
}
}


