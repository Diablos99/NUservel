package NU_Sing_WX;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class FileServer {
    private final int port = 5203;
    private ServerSocket server_socket;   //��ͨ��Ϣ����������׽���
    private ServerSocket fileServerSocket;  // �ļ�����������׽���

    private String path ="D://NU_Songs"; //�������ļ�����·��

    public FileServer() {
        try {
            //1-��ʼ��
            server_socket = new ServerSocket(this.port);   // ������Ϣ���������
            fileServerSocket = new ServerSocket(8888);  //�����ļ����������

              System.out.println("�ļ������������ȴ��ͻ�������.....");
            //2-ÿ�ν���һ���ͻ�����������ʱ������һ���̴߳���
            while(true) {
                Socket client_socket = server_socket.accept();
                 System.out.println("�ͻ���:"+client_socket.getRemoteSocketAddress()+"������");
                new ServerThread(client_socket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private class ServerThread extends Thread{
        private Socket client_socket;
        private BufferedReader server_in;
        private PrintWriter server_out;

        public ServerThread(Socket client_socket) {
            try {
                //��ʼ��
                this.client_socket = client_socket;
                server_in = new BufferedReader(new InputStreamReader(this.client_socket.getInputStream()));
                server_out = new PrintWriter(this.client_socket.getOutputStream(),true);


            } catch (IOException e) {
            }
        }

        public void run() {
            try {
                String uploadFileName = null;
                String uploadFileSize = null;
                String fromClientData ;

                while((fromClientData = server_in.readLine()) != null){
                        //�ѷ������ļ��б���
                        if(fromClientData.startsWith("@action=loadFileList")){
                            File dir = new File(path);
                            if (dir.isDirectory()){
                                String[] list = dir.list();
                                String filelist = "@action=GroupFileList[";
                                for (int i = 0; i < list.length; i++) {
                                    if (i == list.length-1){
                                        filelist  = filelist + list[i]+"]";
                                    }else {
                                        filelist  = filelist + list[i]+":";
                                    }
                                }
                                server_out.println(filelist);
                            }
                        }

                        //�����ϴ��ļ�
                        if (fromClientData.startsWith("@action=Upload")){
                            uploadFileName = ParseDataUtil.getUploadFileName(fromClientData);
                            uploadFileSize = ParseDataUtil.getUploadFileSize(fromClientData);
                            File file = new File(path,uploadFileName);
                            //�ļ��Ƿ��Ѵ���
                            if (file.exists()){
                                //�ļ��Ѿ����������ϴ�
                                server_out.println("@action=Upload[null:null:NO]");
                            }else {
                                //֪ͨ�ͻ��˿������ϴ��ļ�
                                server_out.println("@action=Upload["+uploadFileName+":"+uploadFileSize+":YES]");
                                //�������߳��ϴ��ļ�
                                new HandleFileThread(1,uploadFileName,uploadFileSize).start();
                            }
                        }

                        //���������ļ�
                        if(fromClientData.startsWith("@action=Download")){
                            String fileName = ParseDataUtil.getDownFileName(fromClientData);
                            File file = new File(path,fileName);
                            if(!file.exists()){
                                server_out.println("@action=Download[null:null:�ļ�������]");
                            }else {
                                //֪ͨ�ͻ��˿����������ļ�
                                server_out.println("@action=Download["+file.getName()+":"+file.length()+":OK]");
                                //�������̴߳��������ļ�
                                new HandleFileThread(0,file.getName(),file.length()+"").start();
                                
                            }
                        }
                    }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         *     �ļ������߳�
         */
        class HandleFileThread extends Thread{
            private String filename;
            private String filesize;
            private int mode;  //�ļ�����ģʽ

            public HandleFileThread(int mode,String name,String size){

                    filename = name;
                    filesize = size;
                    this.mode = mode;
            }

            public void run() {
                try {
                    Socket socket = fileServerSocket.accept();
                    //�ϴ��ļ�ģʽ
                    if(mode == 1){
                        //��ʼ�����ϴ�
                        BufferedInputStream file_in = new BufferedInputStream(socket.getInputStream());
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(path,filename)));

                        int len;
                        byte[] arr = new byte[8192];

                        while ((len = file_in.read(arr)) != -1){
                            bos.write(arr,0,len);
                            bos.flush();
                        }
                        server_out.println("@action=Upload[null:null:�ϴ����]");
                        server_out.println("\n");
                        bos.close();
                    }

                    //�����ļ�ģʽ
                    if(mode == 0){
                       BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path,filename)));
                       BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());

                       System.out.println("����������ʼ����");
                       int len;
                       byte[] arr =new byte[8192];
                       while((len = bis.read(arr)) != -1){
                           bos.write(arr,0,len);
                           bos.flush();
                       }

                       socket.shutdownOutput();
                    }
 					
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
	
}
