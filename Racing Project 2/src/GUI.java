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
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		shell = s;
		input ="";
		cmd=0;
		channels = new ArrayList<JLabel>();
		triggers = new ArrayList<JButton>();
		display = new JTextArea();
		display.setEditable(false);
		numPad = new ArrayList<JButton>();
		swapButton = new JButton("Swap");
		commandsButton = new JButton("Commands");
		powerButton = new JButton("Power");

		printPowerButton = new JButton("Printer Power");
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
			//			TODO set font size
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
			numPad.add(b);
		}
		t = new Timer(10, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				updateDisplay();
			}
		});
		t.stop();
		t.setRepeats(true);
		setUpUI();

	}

	private void setUpUI(){
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
		channels = new ArrayList<JLabel>();
		triggers = new ArrayList<JButton>();
		JLabel l;
		JButton b;
		for(int i=0; i<8; ++i){
			l = new JLabel(disabled);
			l.addMouseListener(new ClickListener());
			b = new JButton();
			b.setBackground(Color.BLUE);
			channels.add(l);
			triggers.add(b);
		}
		trigChanGrid.add(new JLabel());
		for(int i=1; i<8; i+=2){
			l = new JLabel(Integer.toString(i));
			l.setHorizontalAlignment(JLabel.CENTER);
			l.setVerticalAlignment(JLabel.BOTTOM);
			trigChanGrid.add(l);
		}
		trigChanGrid.add(new JLabel("Start"));
		for(int i=0; i<7; i+=2){
			trigChanGrid.add(triggers.get(i));
		}
		trigChanGrid.add(new JLabel("Arm/Disarm"));
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
		trigChanGrid.add(new JLabel("Finish"));
		for(int i=1; i<8; i+=2){
			trigChanGrid.add(triggers.get(i));
		}
		trigChanGrid.add(new JLabel("Arm/Disarm"));

		for(int i=1; i<8; i+=2){
			trigChanGrid.add(channels.get(i));
		}

		center.add(title);
		Font f = title.getFont();
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(new Font(f.getName(), f.getStyle(), 16));
		center.add(trigChanGrid);


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
		//		west.add(new JLabel("\n "));
		//		west.add(new JLabel("\n "));
		//		west.add(new JLabel("\n "));
		//		JPanel arrows = new JPanel();
		//		arrows.setLayout(new GridLayout(3,3));
		//		arrows.add(new JLabel());
		//		arrows.add(up);
		//		arrows.add(new JLabel());
		//		arrows.add(left);
		//		arrows.add(new JLabel());
		//		arrows.add(right);
		//		arrows.add(new JLabel());
		//		arrows.add(down);
		//		west.add(arrows);
		add(west, BorderLayout.WEST);
		center.add(display);
		add(center, BorderLayout.CENTER);
		setSize(600, 400);
		//		TODO set up east panel as shown in requirements
		JPanel east = new JPanel();
		east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
		east.add(printPowerButton);
		east.add(printer);
		JPanel numPan = new JPanel();
		numPan.setLayout(new GridLayout(4, 4));
		for(JButton btn : numPad){
			numPan.add(btn);
			btn.addActionListener(new ButtonActions());
		}

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

	}
	private void updateDisplay(){
		Run curRun = shell.getCurrentRun();
		display.append("Time: "+curRun.getElapsedTime() +"\n");
		
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
	private void print(String str){
//		TODO figure out how to word wrap and keep lineCount correct
		if(!printPower) return;
		Scanner sc = new Scanner(str);
		while(sc.hasNextLine()){
			try{
				if((printer.getLineCount()+1)*printer.getFontMetrics(printer.getFont()).getHeight()>=printer.getHeight()){
					System.out.println(45);
					int offset = printer.getLineStartOffset(1);
					String txt = printer.getText();

					printer.setText(txt.substring(offset, txt.length()));
				}
			}catch(Exception es){
				System.out.println(es.toString());
			}
			printer.append(sc.nextLine()+"\n");
		}
	}
	private void commands(int command){
		String str="";
		switch(command){
		case 0:
			str = "1. Event Type\n"+"2. Time\n"+"3. Add Racer\n" + "4. Clear Racer\n" + "5. End Run" + "6. New Run" + "7. Reset\n" + "8. Print\n";
			if(shell.getCurrentRun().getType()==0){
				str += "9. Did Not Finish\n";
			}
			str += "\n";
			display.setText(str);
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
			print(shell.getCurrentRun().print());
			input="";
			cmd=0;
			commandMode=false;
			t.start();
			break;
		case 9:
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
					// TODO Auto-generated method stub
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
		//		TODO
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
		}
		if(badInput){
			display.setText("Invalid Input");
			Timer t2 = new Timer(1000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
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
			shell.readCommand("TRIG" + chan);

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
						input+="1";
						display.append("1");
					}
					break;
				case "3":
					if(commandMode){
						input+="1";
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
						readCommand();
					}
					break;
				}
			}
		}

	}

}


