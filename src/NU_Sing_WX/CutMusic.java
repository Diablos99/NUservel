package NU_Sing_WX;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
/**
 * ���ּ��к�ƴ�ӣ����ִ��գ�
 * @author 
 *
 */
public class CutMusic 
{	
	protected static File NUC;
	protected static int NUC_index = 1;
	protected static CutMusic cutM = new CutMusic();
	public void setIndex(int NUC_index)
	 {
		 this.NUC_index = NUC_index;
	 }
	public int getIndex()
	{
		return NUC_index;
	}
 public static void cut1(String song, int endTime){
	 
	 
	 File f1 = new File(song);
	 try 
		{
		NUC = new File("D://NU_Garbage","CUT"+ NUC_index+ ".mp3");
		NUC_index++;
		NUC.createNewFile();
		}catch (IOException e){
				e.printStackTrace();};
	  BufferedInputStream bis1 = null;
	  BufferedOutputStream bos = null;
	  
	  int start1 = 128*0*1024/8;//320kbps�������ʣ�*58s*1024/8=2375680 �����ʿ��Բ鿴��Ƶ���Ի�֪
	  //Cut Buffer Time
	  
	  int end1 = 128*endTime*1024/8;//320kbps*120s*1024/8=4915200      
	  int tatol1 = 0;
	  try {
	   //����������
	   bis1 = new BufferedInputStream(new FileInputStream(f1));
	
	   //�����ֽ��������true��ʾ���������ĺ���׷�����ݣ������Ǹ��ǣ�����
	   bos = new BufferedOutputStream(new FileOutputStream(NUC,true));
	    
	   //��һ�׸���С�д��
	   byte[] b1= new byte[512];
	   int len1 = 0;
	   while((len1 = bis1.read(b1))!=-1){
	    tatol1+=len1;   //�ۻ�tatol
	    if(tatol1<start1 ){  //tatolС����ʼֵ����������ѭ��
	     continue;
	    }
	    bos.write(b1);   //д��Ķ���������Ԥ��ָ�����ֽڷ�Χ֮��
	    if(tatol1>=end1 ){  //��tatol��ֵ����Ԥ���趨�ķ�Χ��������ˢ��bos�����󣬲�����ѭ��
	     bos.flush();
	     break;
	    }
	     
	   }
	   System.out.println("��һ�׸������ɣ�");
	    
	  
	  } catch (IOException e) {
	   // TODO Auto-generated catch block
	   e.printStackTrace();
	  }finally{
	    try {//�м�Ҫ�ر�������
	     if(bis1!=null) bis1.close();
	     if(bos!=null) bos.close();
	    } catch (IOException e) {
	     // TODO Auto-generated catch block
	     e.printStackTrace();
	    }
	  }
	 }
 
}