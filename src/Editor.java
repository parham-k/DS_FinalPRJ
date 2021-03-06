import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;

public class Editor extends JFrame {

    private Tree_cls tree;
    private JTree treeView;
    private JTextArea txtHTML;
    private JScrollPane treeScrollPane;
    private JPanel pnlMainRight;
    private JEditorPane htmlPageView;

    public static void main(String[] args) {
        (new Editor()).showWindow();
    }

    private Editor() {
        // Initialize data structures
        tree = new Tree_cls();

        // Main Window
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("TableFlipperZ HTML Editor");
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon.jpg")));
        getContentPane().setLayout(new BorderLayout());

        // Menu
        JMenuBar menuBar = new JMenuBar();
        JMenu mnFile = new JMenu("File");
        mnFile.setMnemonic(KeyEvent.VK_F);
        JMenuItem mnFileNew = new JMenuItem("New");
        mnFile.add(mnFileNew);
        JMenuItem mnFileOpen = new JMenuItem("Open");
        mnFile.add(mnFileOpen);
        mnFile.addSeparator();
        JMenuItem mnFileSave = new JMenuItem("Save");
        mnFile.add(mnFileSave);
        mnFile.addSeparator();
        JMenuItem mnFileExit = new JMenuItem("Exit");
        mnFile.add(mnFileExit);
        menuBar.add(mnFile);

        JMenu mnEdit = new JMenu("Edit");
        mnEdit.setMnemonic(KeyEvent.VK_E);
        JMenuItem mnEditAdd = new JMenuItem("Add tag");
        mnEdit.add(mnEditAdd);
        JMenuItem mnEditTag = new JMenuItem("Edit tag");
        mnEdit.add(mnEditTag);
        mnEdit.addSeparator();
        JMenuItem mnEditDeleteTag = new JMenuItem("Delete tag");
        mnEdit.add(mnEditDeleteTag);
        JMenuItem mnEditDeleteSubtree = new JMenuItem("Delete Subtree");
        mnEdit.add(mnEditDeleteSubtree);
        mnEdit.addSeparator();
        JMenuItem mnEditRefresh = new JMenuItem("Refresh");
        mnEdit.add(mnEditRefresh);
        JMenuItem mnEditFont = new JMenuItem("Font size");
        mnEdit.add(mnEditFont);
        menuBar.add(mnEdit);

        JMenu mnView = new JMenu("View");
        mnView.setMnemonic(KeyEvent.VK_V);
        //menuBar.add(mnView);

        setJMenuBar(menuBar);

        // Top panel
        /*JButton btnOpen = new JButton("Open");
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnDeleteSubtree = new JButton("Delete subtree");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnSave = new JButton("Save");
        JComboBox<Integer> cmbxFontSize = new JComboBox<>();
        for (int i = 12; i <= 32; i++)
            cmbxFontSize.addItem(i);
        cmbxFontSize.setSelectedIndex(3);
        JPanel pnlTop = new JPanel(new FlowLayout());
        pnlTop.add(btnOpen);
        pnlTop.add(btnAdd);
        pnlTop.add(btnEdit);
        pnlTop.add(btnDelete);
        pnlTop.add(btnDeleteSubtree);
        pnlTop.add(btnRefresh);
        pnlTop.add(btnSave);
        pnlTop.add(cmbxFontSize);
        getContentPane().add(pnlTop, BorderLayout.NORTH);*/

        // Text Editor
        JPanel pnlMain = new JPanel(new GridLayout(1, 2));
        txtHTML = new JTextArea();
        txtHTML.setTabSize(4);
        txtHTML.setFont(new Font("Courier new", Font.PLAIN, 15));
        pnlMain.add(new JScrollPane(txtHTML));
        pnlMain.setBorder(new EmptyBorder(10, 10, 10, 10));

        pnlMainRight = new JPanel(new GridLayout(2, 1));

        // HTML Viewer
        htmlPageView = new JEditorPane();
        htmlPageView.setEditorKit(new HTMLEditorKit());
        htmlPageView.setEditable(false);
        pnlMainRight.add(new JScrollPane(htmlPageView));

        // TreeView
        treeView = tree.getComponent();
        treeView.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeView.setFont(new Font("Courier new", Font.PLAIN, 15));
        treeScrollPane = new JScrollPane(treeView);
        pnlMainRight.add(treeScrollPane);

        pnlMain.add(pnlMainRight);
        getContentPane().add(pnlMain, BorderLayout.CENTER);
        // Set ActionListeners
        mnFileOpen.addActionListener((ActionEvent e) -> {
            File file = FileIO.getFile();
            tree = new Tree_cls();
            StringSplitter.split(FileIO.read(file, false), tree.getRoot());
            updateTreeView(true);
        });

        mnFileNew.addActionListener((ActionEvent e) -> {
            (new Editor()).showWindow();
            setVisible(false);
        });

        mnFileExit.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        mnEditAdd.addActionListener((ActionEvent e) -> {
            TagNode parentnode = new TagNode();
            parentnode.setTagName(JOptionPane.showInputDialog(this, "Enter parent tag name:", "add node", JOptionPane.QUESTION_MESSAGE));
            ArrayList<ArrayList<TagNode>> paths = new ArrayList<>();
            paths = tree.Search(parentnode);

            PathTable pt = new PathTable(paths);
            pt.setModal(true);
            pt.setVisible(true);
            if (GlobalVariable.SelectedPath != -1)  // this variable's default value is -1
            {
                parentnode = paths.get(GlobalVariable.SelectedPath).get(paths.get(GlobalVariable.SelectedPath).size() - 1);
                AddNodefrm frm = new AddNodefrm();
                frm.setModal(true);
                frm.setVisible(true);

                if (GlobalVariable.tgName != null) {
                    TagNode newnode = new TagNode(parentnode, null, GlobalVariable.tgName, GlobalVariable.tgAtt, GlobalVariable.tgData, GlobalVariable.tgIsSingle);
                    tree.AddNode(parentnode, newnode);

                    GlobalVariable.tgData = null;
                    GlobalVariable.tgAtt = null;
                    GlobalVariable.tgName = null;
                }
                GlobalVariable.SelectedPath = -1;  // again set it to the default
            }


            updateTreeView(true);
        });

        mnEditTag.addActionListener((ActionEvent e) -> {

            // tree.EditNode(JOptionPane.showInputDialog(this, "Enter parent tag name:", "Add tag", JOptionPane.QUESTION_MESSAGE));
            TagNode node = new TagNode();
            node.setTagName(JOptionPane.showInputDialog(this, "Enter tag name:", "Edit tag", JOptionPane.QUESTION_MESSAGE));

            ArrayList<ArrayList<TagNode>> paths = new ArrayList<>();
            paths = tree.Search(node);

            PathTable table = new PathTable(paths);
            table.setModal(true);
            table.setVisible(true);

            if (GlobalVariable.SelectedPath != -1) {
                node = paths.get(GlobalVariable.SelectedPath).get(paths.get(GlobalVariable.SelectedPath).size() - 1);
                EditNode ED = new EditNode(node);
                ED.setModal(true);
                ED.setVisible(true);

                GlobalVariable.SelectedPath = -1;
            }

            updateTreeView(true);
        });

        mnEditDeleteTag.addActionListener((ActionEvent e) -> { //delete without deleting it's children
            TagNode tg = new TagNode();
            tg.setTagName(JOptionPane.showInputDialog(this, "Enter tag name:", "delete node", JOptionPane.QUESTION_MESSAGE));
            ArrayList<ArrayList<TagNode>> paths = new ArrayList<>();
            paths = tree.Search(tg);

            PathTable pt = new PathTable(paths);
            pt.setModal(true);
            pt.setVisible(true);
            tg = paths.get(GlobalVariable.SelectedPath).get(paths.get(GlobalVariable.SelectedPath).size() - 1);
            if (GlobalVariable.SelectedPath != -1)  // this variable's default value is -1
            {
                tree.DeleteNodeKeepChildren(tg);
                GlobalVariable.SelectedPath = -1;  // again set it to the default
            }
            updateTreeView(true);
        });

        mnEditDeleteSubtree.addActionListener((ActionEvent e) -> {
            TagNode tg = new TagNode();
            tg.setTagName(JOptionPane.showInputDialog(this, "Enter tag name:", "delete sub tree", JOptionPane.QUESTION_MESSAGE));
            ArrayList<ArrayList<TagNode>> paths = new ArrayList<>();
            paths = tree.Search(tg);
            PathTable pt = new PathTable(paths);
            pt.setModal(true);
            pt.setVisible(true);
            if (GlobalVariable.SelectedPath != -1)  // this variable's default value is -1
            {
                tree.DeleteNode(paths.get(GlobalVariable.SelectedPath).get(paths.get(GlobalVariable.SelectedPath).size() - 1));
                GlobalVariable.SelectedPath = -1;  // again set it to the default
            }
            updateTreeView(true);

        });

        mnEditRefresh.addActionListener((ActionEvent e) -> {
            tree = new Tree_cls();
            StringSplitter.split(txtHTML.getText(), tree.getRoot());
            updateTreeView(true);
        });

        mnFileSave.addActionListener((ActionEvent e) -> {
            if (tree.getRoot().getChildren() != null || tree.getRoot().getChildren().get(0) != null) {
                FileIO.write(tree.getRoot().getChildren().get(0).toString(0));
            }
        });

        mnEditFont.addActionListener((ActionEvent e) -> {
            int fontSize = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter font size", "Font size", JOptionPane.QUESTION_MESSAGE));
            txtHTML.setFont(new Font("Courier new", Font.PLAIN, fontSize));
            treeView.setFont(new Font("Courier new", Font.PLAIN, fontSize));
        });


        txtHTML.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int tabnum = 0, linenum = -1, end = -1;
                if (e.getKeyCode() == KeyEvent.VK_PERIOD && e.isShiftDown()) // >
                {
                    int caretpos = txtHTML.getCaretPosition();
                    try {
                        linenum = txtHTML.getLineOfOffset(caretpos);
                        end = caretpos - txtHTML.getLineStartOffset(linenum);
                        linenum -= 1;
                    } catch (Exception ex) {
                    }
                    String lines[] = txtHTML.getText().split("\\n");
                    int start = lines[linenum + 1].lastIndexOf("<") + 1;
                    int spaceindx = lines[linenum + 1].lastIndexOf(" ");
                    if (spaceindx > start && spaceindx < end)
                        end = spaceindx;
                    String tgName = lines[linenum + 1].substring(start, end);
                    if (!(tgName.charAt(0) == '/' || tgName.charAt(tgName.length() - 1) == '/')) {
                        txtHTML.insert("</" + tgName + ">", caretpos);
                        txtHTML.setCaretPosition(caretpos);
                        GlobalVariable.autocmpleteflag = true;
                    }
                }
            }


            @Override
            public void keyReleased(KeyEvent e) {

                int tabnum = 0;

                int linenum = -1;
                int caretpos = -1;


                if (e.getKeyCode() == KeyEvent.VK_ENTER) {

                    try {
                        caretpos = txtHTML.getCaretPosition();
                        linenum = txtHTML.getLineOfOffset(caretpos);
                        int columnnum = caretpos - txtHTML.getLineStartOffset(linenum);
                        linenum -= 1;
                    } catch (Exception ex) {
                    }

                    String tabs = "";
                    String lines[] = txtHTML.getText().split("\\n");
                    if (linenum != -1 && linenum >= 0) {
                        tabnum = (lines[linenum].length() - lines[linenum].trim().length());
                    }


                    if (GlobalVariable.autocmpleteflag) {
                        //tabs+="\n";
                        for (int i = 0; i < tabnum + 1; i++) {
                            tabs += "\t";
                        }
                        txtHTML.insert(tabs, txtHTML.getCaretPosition());
                        txtHTML.setCaretPosition(txtHTML.getCaretPosition());
                        tabs = "\n";

                        for (int i = 0; i < tabnum; i++)
                            tabs += "\t";
                        txtHTML.insert(tabs, txtHTML.getCaretPosition());
                        GlobalVariable.autocmpleteflag = false;
                        txtHTML.setCaretPosition(txtHTML.getCaretPosition() - tabnum - 1);
                    } else {
                        for (int i = 0; i < tabnum; i++)
                            tabs += "\t";
                        txtHTML.insert(tabs, txtHTML.getCaretPosition());
                    }
                }

                tree = new Tree_cls();
                StringSplitter.split(txtHTML.getText(), tree.getRoot());
                updateTreeView(false);
            }
        });

    }

    private void showWindow() {
        setSize(800, 600);
        setVisible(true);
    }

    private void updateTreeView(boolean updateText) {
        pnlMainRight.remove(treeScrollPane);
        treeView = tree.getComponent();
        treeScrollPane = new JScrollPane(treeView);
        pnlMainRight.add(treeScrollPane);
        if (updateText && (tree.getRoot().getChildren() != null || tree.getRoot().getChildren().get(0) != null))
            txtHTML.setText(tree.getRoot().getChildren().get(0).toString(0));
        htmlPageView.setText(txtHTML.getText());
        pnlMainRight.revalidate();
    }

}
