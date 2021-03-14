package socialnetwork.utils;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfWriter;
import socialnetwork.domain.User;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PdfGenerator {
    private String pdfName;
    private String title_doc;
    private List<String> lines_doc;
    private String user_doc;
    private static Font font =new Font(Font.FontFamily.TIMES_ROMAN,12,Font.NORMAL);
    private static Font font_title =new Font(Font.FontFamily.TIMES_ROMAN,24,Font.UNDERLINE);
    private static Font font_semititle =new Font(Font.FontFamily.TIMES_ROMAN,15,Font.BOLD);

    public String getTitle_doc() {
        return title_doc;
    }

    public void setTitle_doc(String title_doc) {
        this.title_doc = title_doc;
    }

    public List<String> getLines_doc() {
        return lines_doc;
    }

    public void setLines_doc(List<String> lines_doc) {
        this.lines_doc = lines_doc;
    }

    public String getUser_doc() {
        return user_doc;
    }

    public void setUser_doc(String user_doc) {
        this.user_doc = user_doc;
    }

    public PdfGenerator(String pdfName) {
        this.pdfName = pdfName;
        setUser_doc("");
        setTitle_doc("");
        setLines_doc(new ArrayList<>());
    }


    public void generatePDF(){
        try{
            Document document= new Document();
            PdfWriter.getInstance(document,new FileOutputStream(pdfName));
            document.open();
            this.addMetaData(document);
            this.addTitle(document);
            this.addUserDescription(document);
            this.addContent(document);
            document.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void addUserDescription(Document document) throws DocumentException {
        Paragraph user_description =new Paragraph();
        addBlankSpace(user_description,1);
        user_description.add(new Paragraph("Report for User: "+user_doc,font_semititle));
        addBlankSpace(user_description,1);
        document.add(user_description);
    }

    private void addMetaData(Document document){
       document.addTitle(title_doc);
       document.addSubject("Report for a user");
       document.addKeywords("Java, PDF, iText");
       document.addAuthor(user_doc);
       document.addCreator(user_doc);
    }

    private void addTitle(Document document) throws DocumentException {
        Paragraph title =new Paragraph();
        addBlankSpace(title,1);
        title.add(new Paragraph(title_doc,font_title));
        addBlankSpace(title,1);
        document.add(title);
    }

    private void addContent(Document document) throws DocumentException {
        for(String line:lines_doc){
            document.add(new Paragraph(line,font));
        }
    }

    private static void addBlankSpace(Paragraph paragraph,int nr_lines){
        for(int i=0;i<nr_lines;i++){
            paragraph.add(new Paragraph("\n"));
        }
    }
}
