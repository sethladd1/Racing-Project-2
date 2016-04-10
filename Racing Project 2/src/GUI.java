import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
public class GUI extends JFrame{
	final static ImageIcon enabled = new ImageIcon("Icons/enabledChan.png");
	final static ImageIcon disabled = new ImageIcon("Icons/disabledChan.png");
	private ArrayList<JLabel> channels;
	private ArrayList<JButton> triggers;
	public GUI(){
		JPanel north = new JPanel();
		north.setLayout(new BoxLayout(north, BoxLayout.Y_AXIS));
		JPanel trigChanGrid = new JPanel();
		trigChanGrid.setLayout(new GridLayout(6,5));
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
			b.addActionListener(new TriggerListener());
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

		north.add(title);
		Font f = title.getFont();
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setFont(new Font(f.getName(), f.getStyle(), 16));
		north.add(trigChanGrid);
		add(north, BorderLayout.NORTH);
		setSize(400, 200);;
		
	}
	private class TriggerListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton)e.getSource();
			int chan = triggers.indexOf(btn)+1;
//			TODO trigger sensor chan in current run
			
		}
		
	}
	private class ClickListener extends MouseAdapter{
		public void mousePressed(MouseEvent e){
			JLabel l = (JLabel)e.getSource();
			int minX = l.getWidth()/2 - l.getIcon().getIconWidth()/2;
			int maxX = l.getWidth()/2 + l.getIcon().getIconWidth()/2;
			int minY = l.getHeight()/2 - l.getIcon().getIconHeight()/2;
			int maxY = l.getHeight()/2 + l.getIcon().getIconHeight()/2;
			
			if(e.getX()>minX && e.getX() <maxX 
					&& e.getY()>minY && e.getY()<maxY){
				if(l.getIcon() == disabled){
					l.setIcon(enabled);
					int chan = channels.indexOf(l) + 1;
//					TODO enable channel chan in current run
				}
				else{
					l.setIcon(disabled);
					int chan = channels.indexOf(l) + 1;
//					TODO enable channel chan in current run
				}
			}

		}
	}
}
