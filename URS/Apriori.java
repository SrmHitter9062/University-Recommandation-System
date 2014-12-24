import java.io.*;
import java.util.*;
import java.applet.Applet;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.*;
import java.net.*;
import java.awt.Canvas;
import java.awt.Button;
import java.awt.TextField;
import java.awt.Panel;
import java.awt.Label;
import java.awt.Scrollbar;
import java.awt.Choice;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Event;


class showResult extends Canvas{
  static private Integer minSupportCount = new Integer("2");
  public showResult(){
  }

  public void paint(Graphics page){
      this.setBackground(Color.white);
      AprioriProcess(page);
  }

  static public void setMinSupportCount(Integer count){
    minSupportCount=count;
  }

  static public int getMinSupportCount(){
    return minSupportCount.intValue();
  }
  /**
   * This Generates the Apriori Tables and paints an Applet also
   * Prints the Output in System.out.
   */
  public static void AprioriProcess(Graphics page){
   //Assuming an Minimum Support Count of 2.
    //Integer minSupportCount = new Integer("2");
   //List containing a Map of Candidate Item Sets.
    List CList = new ArrayList();
   //List containing a Map of Large Item Sets.
    List LList = new ArrayList();
   //Input List where each line of input is an entry in this List as a String();
    List input = new ArrayList();
   //Generation of First Candidate Itemset and Large Item Set since the process
   //of creating them is different than the others
   SortedMap C1 = new TreeMap();
   SortedMap L1 = new TreeMap();
   int iterationCount=0;
   java.util.SortedMap tempMap = new TreeMap();
   String buffer,output=new String();
   int count=0;
   boolean goOn=true;
   //Reading the input file and creating first candidate Item set Map.
   //Keys are items. Value is "" in the Map.
   try{
        //FileReader fr = new FileReader("input.txt");
        URL dat_url;
        dat_url = new URL("http://www.cs.wmich.edu/~yang/tlt/cs603/Sujatha/input.txt");
	//BufferedReader in = new BufferedReader(new InputStreamReader(dat_url.openStream()));
	//BufferedReader br = new BufferedReader(fr);
        BufferedReader br = new BufferedReader(new InputStreamReader(dat_url.openStream()));
	while((buffer=br.readLine())!=null){
          StringTokenizer st = new StringTokenizer(buffer,",");
          while(st.hasMoreTokens()){
            C1.put(st.nextToken(),"");
          }
          input.add(buffer);
	}
        //fr.close();
   }catch (IOException e){
    System.out.println("input.txt cannot be opened" );
   }
   //Filling up the values in the Map.
   for(Iterator Iter=C1.keySet().iterator(); Iter.hasNext();){
     count=0;
     String key=(String)Iter.next();
     for(Iterator iter=input.iterator();iter.hasNext();){
       String line= (String)iter.next();
       StringTokenizer st2 = new StringTokenizer(line,",");
       while(st2.hasMoreElements()){
         if( key.equalsIgnoreCase(st2.nextToken()) ){
           count++;
         }
       }
       Integer c = new Integer(count);
       C1.put(key,c);
     }
   }
   CList.add(C1);
   LList.add(generateLMap(C1,minSupportCount));
   //End formation of C1 and L1.
   //Generic Apriori Algorithm.
   System.out.println("---------------------------");
   System.out.println("       Generated Output    ");
   System.out.println("---------------------------");
   while(goOn){
    SortedMap keyMap = new TreeMap();
    //Assuming the Previous Large item set is next Candidate Set
    //check for subsets and add it into candidate set Map.
    SortedMap CMap=(TreeMap)LList.get(iterationCount);
    //this is just for printing purposes.
    SortedMap CMap1=(TreeMap)CList.get(iterationCount);

    //Printing the output
    System.out.println("Candidate Set "+(iterationCount+1));
    for(Iterator i=CMap1.keySet().iterator();i.hasNext();){
      String key= (String)i.next();
      System.out.println(key+":"+CMap1.get(key));
    }
    System.out.println("Large Item Set "+(iterationCount+1));
    for(Iterator i=CMap.keySet().iterator();i.hasNext();){
      String key= (String)i.next();
      System.out.println(key+":"+CMap.get(key));
    }
    //end Printing.
    //Create an array of Strings from each entry in candidate item set which is
    //previous Large item set.
    //Take each item in the array compare with remaining item to see the entries are same
    //except the last one.
    //If they are same join them and add to temp KeyMap by getting the count of the item set entry.
    Object[] arr= CMap.keySet().toArray();
    for(int i=0;i<=arr.length;i++){
      for(int j=i+1;j<arr.length;j++){
        String arr1=(String)arr[i];
        String arr2=(String)arr[j];
        if(iterationCount>=1){
          String temp = arr1.substring(0,arr1.lastIndexOf(","));
          String temp2 = arr2.substring(0,arr2.lastIndexOf(","));
          if(temp.equalsIgnoreCase(temp2)){
              String key =temp+arr1.substring(arr1.lastIndexOf(","),arr1.length())+arr2.substring(arr2.lastIndexOf(","),arr2.length());
              if(isSubSetsPresent(key,CMap))
                keyMap.put(key,getCount(input,key));
          }
        }else{
            String key =(String)arr[i]+","+(String)arr[j];
            keyMap.put(key,getCount(input,key));
        }
      }
    }
    //while loop continues until Large Item set is empty.
    if(!keyMap.isEmpty()){
      CList.add(keyMap);
      LList.add(generateLMap(keyMap,minSupportCount));
      iterationCount++;
    }else{
      goOn=false;
    }
   }
   //end While
   System.out.println("---------------------------");
   System.out.println("       Ends here           ");
   System.out.println("---------------------------");
   //Prints the Maps and Illustrates the Process in the Applet.
   printMaps(CList,LList,input,page,minSupportCount.intValue());
  }

  static void printMaps(List CList,List LList,List input,Graphics page,int minSupportCount){
    int drawcount=1,drawcount2=0;
    SortedMap CMap1 = (SortedMap)CList.get(1);
    page.setColor(new Color(255,255,255));
    page.fill3DRect(0,0,3000,3000,false);
    page.setColor(new Color(0,0,0));
    //page.drawString("Transactional Database D",20,15);
    /**
     * ILLUSTRATION TABLE
     */
    //Draw Transactional Database
    page.setColor(new Color(192,192,192));
    page.fill3DRect(30,30,80,input.size()*20+30,true);
    page.setColor(new Color(0,0,0));
    page.drawRect(30,30,80,20);
    page.drawRect(30,50,80,input.size()*20+10);
    page.setColor(new Color(0,0,0));
    page.setFont(new Font("Arial", Font.PLAIN, 10));
    page.drawString("List of Items",30+2,50-2);
    for(Iterator i=input.iterator();i.hasNext();){
      drawcount++;
      page.drawString(" "+(String)i.next(),30,drawcount*20+30);
    }

    //Arrow Lines
    page.drawString("Generate Candidates",140,(input.size()*20+30)/2-15);
    page.drawString("from Database D",150,(input.size()*20+30)/2+10);
    page.setFont(new Font("Arial", Font.PLAIN, 15));
    page.drawString("- - - - - - - - - - - - - - -",120,(input.size()*20+30)/2);
    page.setFont(new Font("Arial", Font.PLAIN, 20));
    page.drawString(">",240,(input.size()*20+30)/2+3);

    //Draw Candidate set without support count
    drawcount=1;
    page.setFont(new Font("Arial", Font.PLAIN, 10));
    page.drawString("Candidate Item Set  (C)",230,15);
    page.setColor(new Color(253,245,230));
    page.fill3DRect(250,30,40,input.size()*20+30,true);
    page.setColor(new Color(0,0,0));
    page.drawRect(250,30,40,20);
    page.drawRect(250,50,40,input.size()*20+10);
    page.drawString("ItemSet",250+2,50-2);
    for(Iterator i=input.iterator();i.hasNext();){
      drawcount++;
      i.next();
      page.drawString(" "+"{ x }",250+2,drawcount*20+30);
    }
    page.drawString("Scan D for count of ",320,(input.size()*20+30)/2+40-10);
    page.drawString("each Candidate ",330,(input.size()*20+30)/2+40+15);
    page.setFont(new Font("Arial", Font.PLAIN, 15));
    page.drawString("________________________",300,(input.size()*20+30)/2+40);
    page.setFont(new Font("Arial", Font.PLAIN, 20));
    page.drawString(">",480,(input.size()*20+30)/2+50);

    //Draw Candidate set with support count
    drawcount=1;
    page.setFont(new Font("Arial", Font.PLAIN, 10));
    page.drawString("Candidate Item Set with Support Count  (C)",450,15);
    page.setColor(new Color(245,222,179));
    page.fill3DRect(500,30,100,input.size()*20+30,true);
    page.setColor(new Color(0,0,0));
    page.drawRect(500,30,40,20);
    page.drawRect(500+40,30,60,20);
    page.drawRect(500,50,40,input.size()*20+10);
    page.drawString("ItemSet",500+2,50-2);
    page.drawString("Sup.count",500+40+2,50-2);
    page.drawRect(500+40,50,60,input.size()*20+10);
    for(Iterator i=input.iterator();i.hasNext();){
      drawcount++;
      i.next();
      if(drawcount==3 || drawcount ==5){
         page.setColor(new Color(205,92,92));
      }else{
         page.setColor(new Color(0,0,0));
      }
      page.drawString(" "+"{ x }",500+2,drawcount*20+30);
      page.drawString(" y ",500+60,drawcount*20+30);
    }
    page.drawString("Compare candidate support ",610,(input.size()*20+30)/2+40-20);
    page.drawString("count with ",650,(input.size()*20+30)/2+40-5);
    page.drawString("minimum support count ",615,(input.size()*20+30)/2+40+15);
    page.setColor(new Color(205,92,92));
    page.drawString("( MARKED IN RED ) ",630,(input.size()*20+30)/2+40+25);
    page.setColor(new Color(0,0,0));
    page.setFont(new Font("Arial", Font.PLAIN, 15));
    page.drawString("_________________",610,(input.size()*20+30)/2+40);
    page.setFont(new Font("Arial", Font.PLAIN, 20));
    page.drawString(">",735,(input.size()*20+30)/2+50);

    //Draw Large Item set with support count
    //removing from input just to reduce the size of large item set
    input.remove(0);
    drawcount=1;
    page.setFont(new Font("Arial", Font.PLAIN, 10));
    page.drawString("Large Item Set  (L) ",760,15);
    page.setColor(new Color(210,180,140));
    page.fill3DRect(750,30,100,input.size()*20+30,true);
    page.setColor(new Color(0,0,0));
    page.drawRect(750,30,40,20);
    page.drawRect(750+40,30,60,20);
    page.drawRect(750,50,40,input.size()*20+10);
    page.drawString("ItemSet",750+2,50-2);
    page.drawString("Sup.count",750+40+2,50-2);
    page.drawRect(750+40,50,60,input.size()*20+10);
    for(Iterator i=input.iterator();i.hasNext();){
      drawcount++;
      i.next();
      page.drawString(" "+"{ x }",750+2,drawcount*20+30);
      page.drawString(" y ",750+60,drawcount*20+30);
    }

    //Arrow Lines
    page.setFont(new Font("Arial", Font.PLAIN, 15));
    page.drawString("______",860,(input.size()*20+30)/2+40);
    page.setFont(new Font("Arial", Font.PLAIN, 20));
    page.drawString(">",890,(input.size()*20+30)/2+50);
    page.setFont(new Font("Arial", Font.PLAIN, 15));
    for(int i=0;i<50;i++)
      page.drawString("|",908,(input.size()*20+30)/2+52+i*2);
      page.setFont(new Font("Arial", Font.PLAIN, 10));
      page.drawString("Generate new Candidate set until Large Item set = null",640,(input.size()*20+30)/2+62+50*2-15);
      page.setFont(new Font("Arial", Font.PLAIN, 15));
      page.drawString("_________________________________________________________________________________________",200,(input.size()*20+30)/2+52+50*2-1);
      page.setFont(new Font("Arial", Font.PLAIN, 20));
      page.drawString("<",300,(input.size()*20+30)/2+62+50*2-1);
      page.setFont(new Font("Arial", Font.PLAIN, 15));
      for(int i=0;i<50;i++)
        page.drawString("|",200,(input.size()*20+30)/2+52+i*2);
      page.setFont(new Font("Arial", Font.PLAIN, 25));
      page.drawString("^",195,(input.size()*20+30)/2+92);
      page.setFont(new Font("Arial", Font.PLAIN, 15));
      page.drawString("______",200,(input.size()*20+30)/2+40);
      page.setFont(new Font("Arial", Font.PLAIN, 20));
      page.drawString(">",210,(input.size()*20+30)/2+50);

      /**
       * ACTUAL TABLE GENERATED  ....
       */
      for(int i=0;i<CList.size();i++){

        //Candidate set without support count
        drawcount=1;
        SortedMap C1 = (SortedMap)CList.get(i);
        page.setColor(new Color(253,245,230));
        page.fill3DRect(drawcount2*360,drawcount*30-20+300,40,C1.size()*30+30,true);
        page.setColor(new Color(0,0,0));
        page.drawRect(drawcount2*360,drawcount*30-20+300,40,20);
        page.drawRect(drawcount2*360,drawcount*30+300,40,C1.size()*30+10);
        page.setColor(new Color(0,0,0));
        page.setFont(new Font("Arial", Font.PLAIN, 15));
        page.drawString("C"+(i+1),drawcount2*360+2,drawcount*30-2+300-25);
        page.setFont(new Font("Arial", Font.PLAIN, 10));
        page.drawString("ItemSet",drawcount2*360+2,drawcount*30-2+300);
        for(Iterator j=C1.keySet().iterator();j.hasNext();){
         drawcount++;
         String key= (String)j.next();
         int count = itemsetsize(key);
         page.drawString(" {"+key+"}",drawcount2*360,drawcount*30+300);
        }
        //Arrow Lines
        page.drawString("____",(drawcount2)*360+40,C1.size()*30/2+300+20);
        page.drawString("____",(drawcount2)*360+40+120,C1.size()*30/2+300+20);
        page.setFont(new Font("Arial", Font.PLAIN, 15));
        page.drawString(">",(drawcount2)*360+40+132,C1.size()*30/2+300+20+7);
        page.drawString(">",(drawcount2)*360+40+12,C1.size()*30/2+300+20+7);

        //Candidate Item set with support Count
        drawcount=1;
        page.setColor(new Color(245,222,179));
        page.fill3DRect(drawcount2*360+60,drawcount*30-20+300,100,C1.size()*30+30,true);
        page.setColor(new Color(0,0,0));
        page.drawRect(drawcount2*360+60,drawcount*30-20+300,40,20);
        page.drawRect(drawcount2*360+60+40,drawcount*30-20+300,60,20);
        page.drawRect(drawcount2*360+60,drawcount*30+300,40,C1.size()*30+10);
        page.setFont(new Font("Arial", Font.PLAIN, 15));
        page.drawString("C"+(i+1),drawcount2*360+60+2,drawcount*30-2+300-25);
        page.setFont(new Font("Arial", Font.PLAIN, 9));
        page.drawString("with Support Count",drawcount2*360+79+2,drawcount*30-2+300-25);
        page.setFont(new Font("Arial", Font.PLAIN, 10));
        page.drawString("ItemSet",drawcount2*360+60+2,drawcount*30-2+300);
        page.drawString("Sup.count",drawcount2*360+60+42,drawcount*30-2+300);
        page.drawRect(drawcount2*360+60+40,drawcount*30+300,60,C1.size()*30+10);
        for(Iterator j=C1.keySet().iterator();j.hasNext();){
         drawcount++;
         String key= (String)j.next();
         int count = itemsetsize(key);
         if(((Integer)C1.get(key)).intValue()>=minSupportCount){
            page.setColor(new Color(0,0,0));
         }else{
            page.setColor(new Color(205,92,92));
         }
         page.drawString(" {"+key+"}",drawcount2*360+60,drawcount*30+300);
         page.drawString(C1.get(key).toString(),drawcount2*360+60+50,drawcount*30+300);
        }

        //Large Item Set with Support Count
        drawcount=1;
        SortedMap L1 = (SortedMap)LList.get(i);
        page.setColor(new Color(210,180,140));
        page.fill3DRect(drawcount2*360+180,drawcount*30-20+300,100,L1.size()*30+30,true);
        page.setColor(new Color(0,0,0));
        page.drawRect(drawcount2*360+180,drawcount*30-20+300,40,20);
        page.drawRect(drawcount2*360+180+40,drawcount*30-20+300,60,20);
        page.drawRect(drawcount2*360+180,drawcount*30+300,40,L1.size()*30+10);
        page.setFont(new Font("Arial", Font.PLAIN, 15));
        page.drawString("L"+(i+1),drawcount2*360+180+2,drawcount*30-2+300-25);
        page.setFont(new Font("Arial", Font.PLAIN, 9));
        page.drawString("with Support Count",drawcount2*360+19+180+2,drawcount*30-2+300-25);
        page.setFont(new Font("Arial", Font.PLAIN, 10));
        page.drawString("ItemSet",drawcount2*360+180+2,drawcount*30-2+300);
        page.drawString("Sup.count",drawcount2*360+180+42,drawcount*30-2+300);
        page.drawRect(drawcount2*360+180+40,drawcount*30+300,60,L1.size()*30+10);
        for(Iterator j=L1.keySet().iterator();j.hasNext();){
         drawcount++;
         String key= (String)j.next();
         int count = itemsetsize(key);
         page.drawString(" {"+key+"}",drawcount2*360+180,drawcount*30+300);
         page.drawString(L1.get(key).toString(),drawcount2*360+180+50,drawcount*30+300);
        }
        drawcount2++;
    }
  }

  /**
   * Generates a Large Item Set Map from Candidate Itemset Map by comparing with minimum
   * Support Count.
   */
  static public SortedMap generateLMap(SortedMap CMap,Integer minSupportCount){
    SortedMap LMap=new TreeMap();
    for(Iterator i=CMap.keySet().iterator();i.hasNext();){
      String key= (String)i.next();
      if( ((Integer)CMap.get(key)).intValue() >= minSupportCount.intValue() ){
        LMap.put(key,CMap.get(key));
      }
    }
    return LMap;
  }

  /**
   * Returns a boolean value to check if subsets of candidate Item is present in
   * previous large item set. For Example if Candidate Item is {1,2,3}
   * it checks if {1,2}, {2,3} and {1,3} is present in Previous large Item Set.
   */
  static public boolean isSubSetsPresent(String itemset,SortedMap CMap){
    boolean present=false;
    String subsets= gensubset(itemset);
    StringTokenizer st = new StringTokenizer(subsets," ");
    while(st.hasMoreTokens()){
      String key = st.nextToken();
      present=false;
      for(Iterator i=CMap.keySet().iterator();i.hasNext();){
        String ckey= (String)i.next();
        if(key.equalsIgnoreCase(ckey)){
          present=true;
        }
      }
      if(!present)
       return present;
    }
    return present;
  }

    /**
     * Returns the number of times an itemset is present in input.
     */
    static public Integer getCount(List input,String itemset){
      boolean isThere=true;
       int count=0;
          for(Iterator j=input.iterator();j.hasNext();){
             isThere=true;
             String temp=(String)j.next();
             StringTokenizer st=new StringTokenizer(itemset,",");
             while(st.hasMoreTokens()){
               String nextSubset=st.nextToken();
               if( (temp). indexOf(nextSubset)==-1){
                 isThere=false;
               }
             }
             if(isThere){
                count++;
             }
          }
      return new Integer(count);
    }

    /**
     * Generates a String of subsets from Given Itemset.
     * e.x for an input of {1,2,3} it gives an output of
     * {1,2 2,3 3,4}
     */
    static public  String gensubset(String itemset) {
      int len=itemsetsize(itemset);
      int i,j;
      String str1;
      String str2=new String();
      String str3=new String();
      if (len==1)
        return null;
      for (i=1;i<=len;i++) {
        StringTokenizer st=new StringTokenizer(itemset,",");
        str1=new String();
        for (j=1;j<i;j++) {
          str1=str1.concat(st.nextToken());
          str1=str1.concat(",");
        }
        str2=st.nextToken();
        for (j=i+1;j<=len;j++) {
          str1=str1.concat(st.nextToken());
          str1=str1.concat(",");
        }
        if (i!=1)
          str3=str3.concat(" ");
        str3=str3.concat(str1.substring(0,str1.length()-1));
      }
      return str3;
  }

  /**
   * Utility function. Gives a ItemsetSize.
   */
  static public int itemsetsize(String itemset) {
    StringTokenizer st=new StringTokenizer(itemset,",");
    return st.countTokens();
  }
}

public class Apriori extends Applet {
  private TextField text;
  private Panel panel;
  private Panel panel1;
  private Label label;
  private Choice choice;
  private showResult show ;
  private Panel showPanel;

  private Button b1;
  private static String selection = "2";
  //Graphics page=new Graphics();
  public void init()
  {
    this.resize(1000,2000);
    show = new showResult();
    show.resize(2000,1500);
    panel=new Panel();
    panel1 = new Panel();
    showPanel= new Panel();

    label=new Label();
    label.setFont(new Font("Arial", Font.PLAIN, 15));
    label.setText("Select Minimum Support Count and Hit Browser Refresh Button .");
    text=new TextField();
    choice= new Choice();
    choice.add("2");
    choice.add("0");
    choice.add("1");
    choice.add("3");
    choice.add("4");
    choice.select(selection);
    System.out.println("Min Sup Count " +show.getMinSupportCount());
    setLayout(new BorderLayout());
    panel1.setLayout(new BorderLayout());
    panel.add(label);
    panel.add(choice);
    panel.setBackground(Color.lightGray);
    showPanel.add("West",show);
    panel1.add("West",panel);
    this.add("North",panel1);
    this.add("West",showPanel);
  }

       public boolean action(Event e,Object o)
	 {
	  if (e.target instanceof Choice)
		{
                  showResult.setMinSupportCount(new Integer(choice.getSelectedItem()));
                  selection=choice.getSelectedItem();
		}
	  return true;
	}
}
