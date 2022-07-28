package br.com.pdfreport;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.pdfreport.entidades.ItemHeader;
import br.com.pdfreport.entidades.ItemTable;
import br.com.pdfreport.entidades.ItemTotalizer;
import br.com.pdfreport.enuns.Border;
import br.com.pdfreport.enuns.Location;

public class PdfReport {

    private Font BOLD_UNDERLINED = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, new BaseColor(85, 85, 85));
    private Font NORMAL = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private Font CUSTON_COLOR = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, new BaseColor(0, 109, 217));
    private Font CUSTON_COLOR_VALOR = new Font(Font.FontFamily.HELVETICA, 22, Font.ITALIC, new BaseColor(0, 109, 217));

    private List<Object> lObject = new ArrayList<>();

    private Context context;
    private File pdfFile;
    private boolean zebra = false;
    private String fileName = "default";
    private int borderColor = 0xFF000000;

    private int fontTable = 10;

    PdfReport(Context context) {
        this.context = context;
    }

    PdfReport(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    PdfReport(Context context, String fileName, int borderColor) {
        this.context = context;
        this.fileName = fileName;
        this.borderColor = borderColor;
    }


    public PdfReport create() throws Exception {

        pdfFile = createFile();

        OutputStream output = new FileOutputStream(pdfFile);
        Document document = new Document();
        PdfWriter.getInstance(document, output);
        document.open();


        for (Object o : lObject) {
            document.add((Element) o);
        }

        document.close();

        return this;
    }

    private PdfPCell getCellHeader(String legenda, String valor) {
        Paragraph p = new Paragraph();
        p.add(new Phrase(legenda + "\r\n", BOLD_UNDERLINED));
        p.add(new Phrase("" + valor, CUSTON_COLOR));

        PdfPCell cell = new PdfPCell();
        cell.addElement(p);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private PdfPCell getCellHeaderTable(String descricao) {

        Font font = new Font(Font.FontFamily.HELVETICA, fontTable, Font.NORMAL, new BaseColor(255, 255, 255));

        PdfPCell cell = new PdfPCell(new Phrase(descricao, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        cell.setBackgroundColor(new BaseColor(72, 107, 125));

        return cell;
    }

    private PdfPCell getCellLinhaTabela(String value, Location location) {

        Font font = new Font(Font.FontFamily.HELVETICA, fontTable, Font.NORMAL);

        PdfPCell cell = new PdfPCell(new Phrase(value, font));
        cell.setBackgroundColor(zebra ? new BaseColor(238, 238, 238) : new BaseColor(255, 255, 255));
        cell.setBorderColor(new BaseColor(102, 102, 102));

        if(location.equals(Location.RIGTH)){
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        }else if(location.equals(Location.CENTER)){
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        }else{
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        }

        cell.setPadding(5);
        return cell;
    }

    private PdfPCell getCellFooter(String legenda, String valor) {
        Paragraph p = new Paragraph();
        p.add(new Phrase(legenda + "\r\n", BOLD_UNDERLINED));
        p.add(new Phrase(" " + valor, CUSTON_COLOR_VALOR));

        PdfPCell cell = new PdfPCell(p);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(5);
        return cell;
    }

    public PdfReport lineSeparator() throws Exception{
        lObject.add(lineSeparatorIntern(0));
        return this;
    }
    public PdfReport lineSeparator(int height) throws Exception{
        lObject.add(lineSeparatorIntern(height));
        return this;
    }

    private Chunk lineSeparatorIntern(int height) throws Exception{

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(borderColor));
        lineSeparator.setPercentage(100f);
        lineSeparator.setLineWidth((float) height);

        return new Chunk(lineSeparator);
    }


    private Image getImage(String imageName) throws Exception {

        InputStream inputStream = context.getAssets().open(imageName);
        Bitmap bmp = BitmapFactory.decodeStream(inputStream);

        //bmp = resizeBitmap(bmp, 100);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);

        Image image = Image.getInstance(stream.toByteArray());
        image.setAlignment(Image.RIGHT);
        return image;

    }

    public static Bitmap resizeBitmap(Bitmap source, int maxLength) {
        try {
            if (source.getHeight() >= source.getWidth()) {
                int targetHeight = maxLength;
                if (source.getHeight() <= targetHeight) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (targetHeight * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                return result;
            } else {
                int targetWidth = maxLength;

                if (source.getWidth() <= targetWidth) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (targetWidth * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                return result;

            }
        } catch (Exception e) {
            return source;
        }
    }

    private Bitmap resizeImage(Bitmap bitmap, int newSize) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int newWidth = 0;
        int newHeight = 0;

        if (width > height) {
            newWidth = newSize;
            newHeight = (newSize * height) / width;
        } else if (width < height) {
            newHeight = newSize;
            newWidth = (newSize * width) / height;
        } else if (width == height) {
            newHeight = newSize;
            newWidth = newSize;
        }

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                width, height, matrix, true);

        return resizedBitmap;
    }

    public PdfReport imageIn(String imageName, Location location) throws Exception{

        float[] celLayout;

        switch (location) {
            case LEFT: {
                celLayout = new float[]{1f, 2.5f, 2.5f};
                break;
            }
            case CENTER: {
                celLayout = new float[]{2.5f, 1f, 2.5f};
                break;
            }
            default: {
                celLayout = new float[]{2.5f, 2.5f, 1f};
            }
        }

        PdfPTable imageTopPdfPTable = new PdfPTable(celLayout);
        imageTopPdfPTable.getDefaultCell().setBorder(0);

        for(int i = 0; i < 3; i++){

            if(i == 0 && location.equals(Location.LEFT)){
                imageTopPdfPTable.addCell(getImage(imageName));
                continue;
            }

            if(i == 1 && location.equals(Location.CENTER)){
                imageTopPdfPTable.addCell(getImage(imageName));
                continue;
            }

            if(i == 2 && location.equals(Location.RIGTH)){
                imageTopPdfPTable.addCell(getImage(imageName));
                continue;
            }

            imageTopPdfPTable.addCell("");
        }


        lObject.add(imageTopPdfPTable);
        return this;
    }

    public PdfReport headerImage(String imageName, ItemHeader[][] matrizHeader) throws Exception{
        lObject.add(setHeaderImageIntern(imageName, Location.LEFT, Border.YES, matrizHeader));
        return this;
    }

    public PdfReport headerImage(String imageName, Location location, ItemHeader[][] matrizHeader) throws Exception{
        lObject.add(setHeaderImageIntern(imageName, location, Border.YES, matrizHeader));
        return this;
    }

    public PdfReport headerImage(String imageName, Location location, Border border, ItemHeader[][] matrizHeader) throws Exception{
        lObject.add(setHeaderImageIntern(imageName, location, border, matrizHeader));
        return this;
    }

    public PdfReport headerImage(String imageName,  Border border, ItemHeader[][] matrizHeader) throws Exception{
        lObject.add(setHeaderImageIntern(imageName, Location.LEFT, border, matrizHeader));
        return this;
    }

    private PdfPTable setHeaderImageIntern(String imageName, Location location, Border border, ItemHeader[][] matrizHeader) throws Exception{

        if(matrizHeader == null){
            throw new Exception("itemCells is empty!");
        }

        float[] celLayout;

        switch (location) {
            case RIGTH: {
                celLayout = new float[]{5f, 1f};
                break;
            }
            default: {
                celLayout = new float[]{1f, 5f};
            }
        }

        PdfPTable headerImagePdfPTable = new PdfPTable(celLayout);
        headerImagePdfPTable.getDefaultCell().setBorderColor(new BaseColor(borderColor));

        if(border.equals(Border.NO)) {
            headerImagePdfPTable.getDefaultCell().setBorder(0);
        }

        headerImagePdfPTable.setWidthPercentage(100);
        headerImagePdfPTable.setSpacingBefore(10);
        headerImagePdfPTable.setSpacingAfter(10);


        PdfPTable headerInterno = new PdfPTable(matrizHeader[0].length);

        for (int i = 0; i < matrizHeader.length; i++) {

            for (int j = 0; j < matrizHeader[i].length; j++) {

                ItemHeader itemCell = matrizHeader[i][j];

                headerInterno.addCell(getCellHeader(itemCell.getSubtitle(), itemCell.getTitle()));

            }

        }

        if(location.equals(Location.LEFT)){
            headerImagePdfPTable.addCell(getImage(imageName));
            headerImagePdfPTable.addCell(headerInterno);
        }else{
            headerImagePdfPTable.addCell(headerInterno);
            headerImagePdfPTable.addCell(getImage(imageName));
        }

        return headerImagePdfPTable;

    }

    public PdfReport header(ItemHeader[][] matrizHeader) throws Exception{
        lObject.add(setHeaderIntern(Border.YES, matrizHeader));
        return this;
    }

    public PdfReport header(Border border, ItemHeader[][] matrizHeader) throws Exception{
        lObject.add(setHeaderIntern(border, matrizHeader));
        return this;
    }

    private PdfPTable setHeaderIntern(Border border, ItemHeader[][] matrizHeader) throws Exception{

        if(matrizHeader == null){
            throw new Exception("itemCells is empty!");
        }

        PdfPTable pdfPTable = new PdfPTable(matrizHeader[0].length);
        pdfPTable.getDefaultCell().setBorderColor(new BaseColor(borderColor));

        if(border.equals(Border.NO)) {
            pdfPTable.getDefaultCell().setBorder(0);
        }

        pdfPTable.setWidthPercentage(100);
        pdfPTable.setSpacingBefore(10);
        pdfPTable.setSpacingAfter(10);


        for (int i = 0; i < matrizHeader.length; i++) {

            for (int j = 0; j < matrizHeader[i].length; j++) {

                ItemHeader itemHeader = matrizHeader[i][j];

                pdfPTable.addCell(getCellHeader(itemHeader.getSubtitle(), itemHeader.getTitle()));

            }

        }

        return pdfPTable;

    }

    public PdfReport table(float[] columnWidths, String[] arraySubtitle, ItemTable[][] matrizTable, int fontTable) throws Exception{
        this.fontTable = fontTable;
        lObject.add(setTableIntern(columnWidths, arraySubtitle, matrizTable));
        return this;
    }

    public PdfReport table(float[] columnWidths, String[] arraySubtitle, ItemTable[][] matrizTable) throws Exception{
        lObject.add(setTableIntern(columnWidths, arraySubtitle, matrizTable));
        return this;
    }

    private PdfPTable setTableIntern(float[] columnWidths, String[] arraySubtitle, ItemTable[][] matrizTable) throws Exception{

        if(columnWidths == null){
            throw new Exception("columnWidths is empty!");
        }

        if(arraySubtitle == null){
            throw new Exception("arraySubtitle is empty!");
        }

        if(matrizTable == null){
            throw new Exception("arrayTabela is empty!");
        }

        PdfPTable pdfPTable = new PdfPTable(columnWidths);

        pdfPTable.setWidthPercentage(100);
        pdfPTable.setSpacingBefore(10);
        pdfPTable.setSpacingAfter(10);

        for (String subtitle : arraySubtitle) {
            pdfPTable.addCell(getCellHeaderTable(subtitle));
        }

        for (int i = 0; i < matrizTable.length; i++) {


            for (int j = 0; j < matrizTable[i].length; j++) {

                ItemTable itemTable = matrizTable[i][j];
                pdfPTable.addCell(getCellLinhaTabela(itemTable.getTitle(), itemTable.getLocation()));

            }

            zebra = !zebra;

        }

        return pdfPTable;

    }

    public PdfReport totalizer(ItemTotalizer[] arrayTotalizer) throws Exception{
        lObject.add(setTotalizerIntern(arrayTotalizer));
        return this;
    }

    private PdfPTable setTotalizerIntern(ItemTotalizer[] arrayTotalizer) throws Exception{

        PdfPTable pdfPTable  = new PdfPTable(3);
        
        pdfPTable.setWidthPercentage(100);
        pdfPTable.setSpacingBefore(10);

        if(arrayTotalizer.length == 1){
            pdfPTable.addCell(getCellFooter("", ""));
            pdfPTable.addCell(getCellFooter("", ""));
            pdfPTable.addCell(getCellFooter(arrayTotalizer[0].getSubtitle(), arrayTotalizer[0].getTitle()));

        }else if(arrayTotalizer.length == 2){
            pdfPTable.addCell(getCellFooter("", ""));
            pdfPTable.addCell(getCellFooter(arrayTotalizer[0].getSubtitle(), arrayTotalizer[0].getTitle()));
            pdfPTable.addCell(getCellFooter(arrayTotalizer[1].getSubtitle(), arrayTotalizer[1].getTitle()));

        }else if(arrayTotalizer.length == 3){
            pdfPTable.addCell(getCellFooter(arrayTotalizer[0].getSubtitle(), arrayTotalizer[0].getTitle()));
            pdfPTable.addCell(getCellFooter(arrayTotalizer[1].getSubtitle(), arrayTotalizer[1].getTitle()));
            pdfPTable.addCell(getCellFooter(arrayTotalizer[2].getSubtitle(), arrayTotalizer[2].getTitle()));
        }

        return pdfPTable;

    }


    public File createFile() throws Exception {

        String nomePdf = fileName + ".pdf";

        File folder = new File(Environment.getExternalStorageDirectory() + "/Documents");
        if (!folder.exists()) {
            folder.mkdir();
        }

        File file = new File(folder.getAbsolutePath(), nomePdf);

        if (file.exists()) {
            if (!file.delete()) {
                throw new Exception("Could not delete the file!");
            }
        }

        if (!file.createNewFile()) {
            throw new Exception("Failed to create file!");
        }

        return file;
    }

    public void sendPdf() throws Exception {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);

        Uri uri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(Objects.requireNonNull(context), context.getPackageName()+".provider", pdfFile);

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(pdfFile);
        }

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("*/*");
        context.startActivity(intent);

    }

    public void previewPdf() throws Exception {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(Objects.requireNonNull(context), context.getPackageName()+".provider", pdfFile);

            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(pdfFile);
        }

        intent.setDataAndType(uri, "application/pdf");
        context.startActivity(intent);
    }


    public static PdfReport init(Context context) {
        return new PdfReport(context);
    }

    public static PdfReport init(Context context, String fileName) {
        return new PdfReport(context, fileName);
    }

    public static PdfReport init(Context context, String fileName, int borderColor) {
        return new PdfReport(context, fileName, borderColor);
    }



}
