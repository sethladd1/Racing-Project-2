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
	final static ImageIcon leftArrow = new ImageIcon("Icons/leftArrow.png");
	final static ImageIcon rightArrow = new ImageIcon("Icons/rightArrow.png");
	final static ImageIcon upArrow = new ImageIcon("Icons/upArrow.png");
	final static ImageIcon downArrow = new ImageIcon("Icons/downArrow.png");
	private ArrayList<JLabel> channels;
	private ArrayList<JButton> triggers;
	private ArrayList<JButton> numPad;
	private JTextArea display;
	private JTextPane printer;
	private JButton swap, power, function, printerPower;
	private JLabel left, right, down, up;
	private Timer t;
	private Run curRun;
	private ArrayList<Run> runs;

	public GUI(Run r){
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		if(r!=null)
			curRun = r;
		else 
			curRun = new Run(0,1);
		runs = new ArrayList<Run>();
		runs.add(curRun);
		channels = new ArrayList<JLabel>();
		triggers = new ArrayList<JButton>();
		display = new JTextArea();
		display.setEditable(false);
		numPad = new ArrayList<JButton>();
		swap = new JButton("SWAP");
		function = new JButton("FUNCTION");
		power = new JButton("Power");
		printerPower = new JButton("Print Power");
		left = new JLabel(leftArrow);
		right = new JLabel(rightArrow);
		up = new JLabel(upArrow);
		down = new JLabel(downArrow);

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
		t = new Timer(100, new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				updateDisplay();
			}
		});
		t.stop();
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
		BoxLayout blo = new BoxLayout(west, BoxLayout.Y_AXIS);
		west.setAlignmentX(LEFT_ALIGNMENT);
		west.setLayout(blo);
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		power.setAlignmentX(LEFT_ALIGNMENT);
		west.add(power);
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(function);
		function.setAlignmentX(LEFT_ALIGNMENT);
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		west.add(new JLabel("\n "));
		JPanel arrows = new JPanel();
		arrows.setLayout(new GridLayout(3,3));
		arrows.add(new JLabel());
		arrows.add(up);
		arrows.add(new JLabel());
		arrows.add(left);
		arrows.add(new JLabel());
		arrows.add(right);
		arrows.add(new JLabel());
		arrows.add(down);
		west.add(arrows);
		add(west, BorderLayout.WEST);
		center.add(display);
		add(center, BorderLayout.CENTER);
		setSize(600, 400);
//		TODO set up east panel as shown in requirements
	}
	/**
	 * called by shell if a channel toggled to ensure it shows in the GUI
	 */
	public void syncChanIcons(){
		for(int i=1;i<9; i++){
			if(curRun.getChannel(i)){
				channels.get(i-1).setIcon(enabled);
			}
			else{
				channels.get(i-1).setIcon(disabled);
			}
		}
	}
	/**
	 * called by shell when new run command is entered to ensure GUI and shell are working with the same run. 
	 * @param r - the new current run
	 * @return - true if current run has ended, else false
	 */
	public boolean setRun(Run r){
		if(curRun.running()){
			return false;
		}
		curRun=r!=null?r:new Run(0,curRun.getRunNum()+1);
		return true;
	}
	public Run getCurrentRun(){
		return curRun;
	}
	private void updateDisplay(){
//		TODO update the display as described in project requirements using curRun's finishQueues, StartQueues, and lastFinishers
//		in PARIND runs we will display two list side by side
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
				if(icon==upArrow){
					centerX = l.getWidth()/2;
					centerY = maxY;
					x = Math.abs(e.getX()-centerX);

					y = -1*(e.getY()-centerY);

					if(x<icon.getIconWidth()/2&&y<icon.getIconHeight()-2*x && y>=0){
						System.out.println("clicked up");
					}

				}
				else if(icon==rightArrow){
					//					TODO calculated clickable area for right 

				}else if(icon==leftArrow){
					//					TODO calculated clickable area for left 

				}else if(icon==downArrow){
					//					TODO calculated clickable area for down 

				}
			}

		}
	}
	private class TriggerListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton)e.getSource();
			int chan = triggers.indexOf(btn)+1;
			//			TODO trigger sensor chan in current run

		}

	}

}


