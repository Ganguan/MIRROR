import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JMenuBar;
import java.awt.Choice;
import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.TextArea;
import java.awt.geom.RoundRectangle2D;
import java.awt.Button;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.JToggleButton;
import javax.swing.JSeparator;
import javax.swing.JTable;
import java.awt.Canvas;
import java.awt.SystemColor;
import javax.swing.JButton;
import com.sun.awt.AWTUtilities;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class myref extends JFrame {

	private JFrame frame;
	private JPanel contentPane;
	private JTextField txtXercesj;
	private JTextField txtNsgaii;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					myref frame = new myref();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public myref() {
		setBackground(SystemColor.activeCaption);
		setTitle("MyRef-Recommendation");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		AWTUtilities.setWindowShape(frame,new RoundRectangle2D.Double(0.0D,0.0D,frame.getWidth(),frame.getHeight(),40.0D,40.0D));
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel lblNewLabel = new JLabel("RefactoringType");
		lblNewLabel.setFont(new Font("Calibri", Font.PLAIN, 14));
		
		JLabel lblProject = new JLabel("Source Program");
		lblProject.setFont(new Font("Calibri", Font.PLAIN, 14));
		
		txtXercesj = new JTextField();
		txtXercesj.setFont(new Font("Calibri", Font.PLAIN, 14));
		txtXercesj.setText("Xerces-J");
		txtXercesj.setColumns(10);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setFont(new Font("Calibri", Font.PLAIN, 14));
		comboBox.setEditable(true);
		
		comboBox.addItem("Decrease Field Visibility");
		comboBox.addItem("Increase Field Visibility");
		comboBox.addItem("Make Field Final");
		comboBox.addItem("Make Field NonFinal");
		comboBox.addItem("Make Field NonStatic");
		comboBox.addItem("Make Field Static");
		comboBox.addItem("Make Field Down");
		comboBox.addItem("Make Field Up");
		comboBox.addItem("Remove Field");
		comboBox.addItem("Decrease Method Visibility");
		comboBox.addItem("Increase Method Visibility");
		comboBox.addItem("Make Method Final");
		comboBox.addItem("Make Method NonFinal");
		comboBox.addItem("Make Method NonStatic");
		comboBox.addItem("Make Method Static");
		comboBox.addItem("Make Method Down");
		comboBox.addItem("Make Method Up");
		comboBox.addItem("Remove Method");
		comboBox.addItem("Collapse Hierarchy");
		comboBox.addItem("Extract Subclass");
		comboBox.addItem("Make Class Concrete");
		comboBox.addItem("Make Class Abstract");
		comboBox.addItem("Make Class Final");
		comboBox.addItem("Make Class NonFinal");
		comboBox.addItem("Remove Class");
		comboBox.addItem("Remove Interface");
		
		comboBox.addItem("");
		comboBox.addItem("");
		contentPane.add(lblNewLabel);
		contentPane.add(comboBox);
		setVisible(true);
		
		JSeparator separator = new JSeparator();
		
		JButton btnNewButton = new JButton("Refactor");
		btnNewButton.setBackground(SystemColor.activeCaption);
		btnNewButton.setFont(new Font("Calibri", Font.PLAIN, 14));
		
		JButton btnCancle = new JButton("Cancle");
		btnCancle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCancle.setBackground(SystemColor.activeCaption);
		btnCancle.setFont(new Font("Calibri", Font.PLAIN, 14));
		
		JLabel lblGeneticAlgorithm = new JLabel("Genetic Algorithm");
		lblGeneticAlgorithm.setFont(new Font("Calibri", Font.PLAIN, 14));
		
		txtNsgaii = new JTextField();
		txtNsgaii.setText("NSGA-II");
		txtNsgaii.setFont(new Font("Calibri", Font.PLAIN, 14));
		txtNsgaii.setColumns(10);
		
		JLabel lblObjective = new JLabel("Objective");
		lblObjective.setFont(new Font("Calibri", Font.PLAIN, 14));
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setFont(new Font("Calibri", Font.PLAIN, 14));
		comboBox_1.setEditable(true);
		
		comboBox_1.addItem("Quality");
		comboBox_1.addItem("Code Smells");
		comboBox_1.addItem("Historical consistency");
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(22)
							.addComponent(lblProject)
							.addGap(49)
							.addComponent(txtXercesj, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(separator, GroupLayout.PREFERRED_SIZE, 423, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(22)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblGeneticAlgorithm, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel)
								.addComponent(lblObjective, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 279, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(txtNsgaii, GroupLayout.PREFERRED_SIZE, 116, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 94, Short.MAX_VALUE)
									.addComponent(btnNewButton))
								.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
									.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
									.addComponent(btnCancle, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)))))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(24)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblProject)
						.addComponent(txtXercesj, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(28)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 3, GroupLayout.PREFERRED_SIZE)
					.addGap(27)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(btnNewButton)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblGeneticAlgorithm, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
							.addComponent(txtNsgaii, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)))
					.addGap(24)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblObjective, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
						.addComponent(comboBox_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnCancle, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
}
