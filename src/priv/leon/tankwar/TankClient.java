package priv.leon.tankwar;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
/**
 * @author Leon
 * 主窗口类
 */

public class TankClient extends Frame
{
	private static final long serialVersionUID = 1L;
	
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	private int score = 0;
	private int addTanks=8;
	
	Wall w1 = new Wall(100,200,20,150,this), w2 = new Wall(300,100,300,20,this);
	Wall w3 = new Wall(160,500,300,25,this), w4 = new Wall(650,280,25,180,this);
	
	Tank myTank = new Tank(200,560,true,Tank.Direction.STOP,this);
	List <Tank> tanks = new ArrayList <Tank>();
	List <Explode> explodes = new ArrayList <Explode>();
	List <Missile> missiles = new ArrayList <Missile> ();
			
	Image offScreenImage = null;
	
	public static void main(String[] args)
	{
		TankClient tc = new TankClient();
		tc.lauchFrame();
	}
	
	@Override
	public void paint(Graphics g)
	{
		/**
		 * 游戏当前各种属性
		 */
		g.drawString("发射子弹数:"+missiles.size(), 10, 50);
		g.drawString("发生爆炸数:"+explodes.size(), 10, 70);
		g.drawString("敌人坦克数:"+tanks.size(), 10, 90);
		g.drawString("得分:"+this.score, 10, 110);
		g.drawString("生命值:"+myTank.getLife(), 10, 130);
		
		
		if(tanks.size()<=3)
		{
			for(int i= 0;i<addTanks;i++)
			{
				tanks.add(new Tank(50+40*(i+1),50,false,Tank.Direction.D,this));
			}
			addTanks++;
		}
		
		myTank.draw(g);
		//以下控制玩家可否穿墙
		myTank.collidesWithWall(w1);
		myTank.collidesWithWall(w2);
		myTank.collidesWithWall(w3);
		myTank.collidesWithWall(w4);
		myTank.collidesWithTanks(tanks);
		w1.draw(g);
		w2.draw(g);
		w3.draw(g);
		w4.draw(g);
		
		for(int i=0;i<tanks.size();i++)
		{
			Tank t = tanks.get(i);
			t.collidesWithWall(w1);
			t.collidesWithWall(w2);
			t.collidesWithWall(w3);
			t.collidesWithWall(w4);
			t.collidesWithTanks(tanks);
			t.draw(g);
		}
		for(int i=0;i<missiles.size();i++)
		{
			Missile m = missiles.get(i);
			if (m.hitTanks(tanks))
			{
				score++;
			}
			m.hitTank(myTank);
			m.hitWall(w1);
			m.hitWall(w2);
			m.hitWall(w3);
			m.hitWall(w4);
			m.draw(g);
		}
		for(int i=0;i<explodes.size();i++)
		{
			Explode e = explodes.get(i);
			e.draw(g);
		}
		
		if(!myTank.isLive()&&this.score>=100)
		{
			g.drawString("神一般的手速！！！ 666!!!", 300, 300);
		}
	}
	
	@Override
	public void update(Graphics g)
	{
		if(offScreenImage == null)
		{
			offScreenImage = this.createImage(GAME_WIDTH,GAME_HEIGHT);
		}
		Graphics goffScreen = offScreenImage.getGraphics();
		
		Color c = goffScreen.getColor();
		goffScreen.setColor(Color.GREEN);
		goffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		goffScreen.setColor(c);
		
		paint(goffScreen);
		
		g.drawImage(offScreenImage, 0, 0, null);
	}
	
	/**
	 * 显示主窗口
	 */
	public void lauchFrame()
	{
		for(int i= 0;i<10;i++)
		{
			tanks.add(new Tank(50+40*(i+1),50,false,Tank.Direction.D,this));
		}
		
		this.setTitle("坦克大战BiuBiuBiu~");
		this.setLocation(300, 60);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		//匿名类,控制窗口关闭
		this.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		this.setResizable(false);
		this.setBackground(Color.GREEN);
		this.addKeyListener(new KeyMonitor());
		
		setVisible(true);
		
		new Thread(new PaintThread()).start();
	}
	
	
	private class PaintThread implements Runnable
	{
		@Override
		public void run()
		{
			while(true)
			{
			 	repaint();
				try
				{
					Thread.sleep(100);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private class KeyMonitor extends KeyAdapter
	{
		@Override
		public void keyReleased(KeyEvent e)
		{
			myTank.keyReleased(e);
		}

		@Override
		public void keyPressed(KeyEvent e)
		{
			myTank.keyPressed(e);
		}
	}
	
	public void setScore(int score)
	{
		this.score = score;
	}

	public int getScore()
	{
		return score;
	}
}
