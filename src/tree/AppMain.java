/*
 	java���� ��ü������ MP3�� ��� �� �м��ϴ� ����� ����.
 	������ ���¼ҽ� ������ apache ���� �����ϴ� PIKA��� ����� ����ͼ� �������.
 */

package tree;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.LyricsHandler;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;



public class AppMain extends JFrame implements TreeSelectionListener {
	JPanel p_west, p_center;
	JTree tree;
	JScrollPane scroll;
	DefaultMutableTreeNode root = null;
	JTextArea area;
	String path = "C:/java_workspace2/0412TreeProject/data/";
	String fileLocation;
	Thread thread;
	boolean flag = true;
	
	public AppMain() {
		p_west = new JPanel();
		p_center = new JPanel();

		// createNode();
		// createDirectory();
		createMusicDir();

		tree = new JTree(root);
		scroll = new JScrollPane(tree);
		area = new JTextArea();

		p_west.setPreferredSize(new Dimension(150, 500));
		p_west.setLayout(new BorderLayout()); // borderLayout�� ������ �ٴ� ���� ũ��
		p_west.add(scroll);
		add(p_west, BorderLayout.WEST);
		add(area);

		// tree�� ������ ����
		tree.addTreeSelectionListener(this);

		setSize(700, 500);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	public void createNode() {
		// �ֻ��� ��� �����ϱ�
		root = new DefaultMutableTreeNode("����");

		DefaultMutableTreeNode node1 = null;
		DefaultMutableTreeNode node2 = null;
		DefaultMutableTreeNode node3 = null;

		DefaultMutableTreeNode out1 = null;
		DefaultMutableTreeNode out2 = null;
		DefaultMutableTreeNode out3 = null;

		node1 = new DefaultMutableTreeNode("��纣��");
		node2 = new DefaultMutableTreeNode("����");
		node3 = new DefaultMutableTreeNode("���Ի����");

		out1 = new DefaultMutableTreeNode("����");
		out2 = new DefaultMutableTreeNode("Ű��");
		out3 = new DefaultMutableTreeNode("�ٳ���");

		node3.add(out1);
		node3.add(out2);
		node3.add(out3);

		root.add(node1);
		root.add(node2);
		root.add(node3);

	}

	// �������� ������ �����ֱ�(���� Ž����)
	public void createDirectory() {
		root = new DefaultMutableTreeNode("����ǻ��");

		File[] drive = File.listRoots();

		// ��ũ ���� ������ ǥ�����ش�
		FileSystemView fsv = FileSystemView.getFileSystemView(); // �߻�Ŭ����������
																	// �޼��带 ���캸��
																	// �ڽ��� �ҷ�����
																	// �ʾƵ� ������ ��
																	// �ִ� ����� �ִ�
																	// ��찡 �ִ�.

		for (int i = 0; i < drive.length; i++) {
			DefaultMutableTreeNode node = null;
			String volume = fsv.getSystemDisplayName(drive[i]);

			node = new DefaultMutableTreeNode(volume);
			root.add(node);
		}
	}

	// ����ã�� ����ϱ�
	public void createMusicDir() {
		root = new DefaultMutableTreeNode("��ũ�ڽ�");

		File file = new File(path);
		File[] child = file.listFiles();

		for (int i = 0; i < child.length; i++) {
			DefaultMutableTreeNode node = null;
			node = new DefaultMutableTreeNode(child[i].getName());
			root.add(node);

		}

	}

	// ������ node�� ���Ͽ� ���� ���� �����ϱ�
	public void extract(String filename) {
		//System.out.println(filename);

		fileLocation = path+filename;

		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
		FileInputStream inputstream = null;
		
		try {
			inputstream = new FileInputStream(new File(fileLocation));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		ParseContext pcontext = new ParseContext();
		// Mp3 parser
		Mp3Parser Mp3Parser = new Mp3Parser();
		LyricsHandler lyrics;
		
		try {
			Mp3Parser.parse(inputstream, handler, metadata, pcontext);
			lyrics = new LyricsHandler(inputstream, handler);
			while (lyrics.hasLyrics()) {
				area.append(lyrics.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (TikaException e) {
			e.printStackTrace();
		}

		area.append("Contents of the document:" + handler.toString());
		area.append("Metadata of the document:");
		
		String[] metadataNames = metadata.names();

		for (String name : metadataNames) {
			area.append(name + ": " + metadata.get(name));
		}
		
	}

	//������ MP3 ���� ���, JLayer
	public void play(){
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(new File(fileLocation));
			AdvancedPlayer player = new AdvancedPlayer(fis);
			if(flag==true){
				player.play();
				flag = false;
			} else if(flag == false){
				player.stop();
				player.play();
				flag = true;
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JavaLayerException e) {
			e.printStackTrace();
		}
	}
	
	public void valueChanged(TreeSelectionEvent e) { // �̺�Ʈ�� �߻���Ű�� ��ü�� tree
		// System.out.println("����?");
		Object obj = e.getSource();
		JTree tree = (JTree) obj; // ������Ʈ�����δ� �� �� �ִ°� ���� ���� ������ JTree������ �ٲ��ش�.
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent(); // ����ȯ
		// System.out.println(node.getUserObject()); //sysout�� ������Ʈ���� ���� �� ����.
		// ������ ������Ʈ ���� ��½� String���� �ٲ��ִ� ����� ���� �ֱ� ������ ��°���
		extract(node.getUserObject().toString()); // ��Ʈ�������� ��ȯ.
		
		thread = new Thread(){
			public void run(){
				play();
			}
		};
		thread.start();
		
	}
	
	
	

	public static void main(String[] args) {
		new AppMain();
	}

}
