import javax.swing.*;
import javax.swing.plaf.basic.BasicOptionPaneUI.ButtonAreaLayout;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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

	private void updateDisplay(){
		//		TODO update the display as described in project requirements using curRun's finishQueues, StartQueues, and lastFinishers
		Run curRun = shell.getCurrentRun();
		switch(curRun.getType()){
		case 0:
			ArrayList<Racer> rcrs = curRun.getRacers();
			for(Racer r : rcrs){

			}
			break;
		case 1:
			break;
		case 2:
			break;
		}
	}
	private void print(){
		
	}
	private void commands(int cmd){
		String str;
		switch(cmd){
		case 0:
			str = "1. Event Type\n"+"2. Time\n"+"3. Add Racer\n" + "4. Clear Racer\n" + "5. End Run" + "6. New Run" + "7. Reset\n" + "8. Print\n";
			if(shell.getCurrentRun().getType()==0){
				str += "8. Did Not Finish\n";
			}
			str += "\n";
			display.setText(str);
			break;
		case 1:
			str = "1. IND\n"+"2. PARIND\n"+"3.GRP\n\n";
		}
		
	}
	private void readCommand(){
		
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
			}
			//			else{
			//				if(icon==upArrow){
			//					centerX = l.getWidth()/2;
			//					centerY = maxY;
			//					x = Math.abs(e.getX()-centerX);
			//
			//					y = -1*(e.getY()-centerY);
			//
			//					if(x<icon.getIconWidth()/2&&y<icon.getIconHeight()-2*x && y>=0){
			//						System.out.println("clicked up");
			//					}
			//
			//				}
			//				else if(icon==rightArrow){

			//
			//				}else if(icon==leftArrow){

			//
			//				}else if(icon==downArrow){

			//
			//				}
			//			}

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
			//			TODO: handle commandsButton, powerButton, swapButton and printPowerButton 
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
					t.stop();
					commandMode = true;
					break;
				case "Swap":
					shell.readCommand("SWAP");
					if(!shell.getErrorMessage().isEmpty()){
						
					}
					break;
				case "1":
					if(commandMode){
						input+="1";
						display.setText(display.getText()+"1");
					}
					break;
				case "2":
					if(commandMode){
						input+="1";
						display.setText(display.getText()+"1");
					}
					break;
				case "3":
					if(commandMode){
						input+="1";
						display.setText(display.getText()+"3");
					}
					break;
				case "4":
					if(commandMode){
						input+="4";
						display.setText(display.getText()+"4");
					}
					break;
				case "5":
					if(commandMode){
						input+="5";
						display.setText(display.getText()+"5");
					}
					break;
				case "6":
					if(commandMode){
						input+="6";
						display.setText(display.getText()+"6");
					}
					break;
				case "7":
					if(commandMode){
						input+="7";
						display.setText(display.getText()+"7");
					}
					break;
				case "8":
					if(commandMode){
						input+="8";
						display.setText(display.getText()+"8");
					}
					break;
				case "9":
					if(commandMode){
						input+="9";
						display.setText(display.getText()+"9");
					}
					break;
				case "0":
					if(commandMode){
						input+="0";
						display.setText(display.getText()+"0");
					}
					break;
				case "*":
					
					break;
				case "#":
					break;
				}
			}
		}

	}

}


