package priv.leon.tankwar;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;
/**
 * 坦克类
 * @author Leon
 *
 */
public class Tank
{
	
	public static final int XSPEED = 5;
	public static final int YSPEED = 5;
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	
	private int x, y;
	private int oldX,oldY;
	private boolean good;
	private boolean live = true;
	private int life = 3;//生命值
	
	private boolean bl = false,bu=false,br=false,bd=false;
	enum Direction{L,LU,U,RU,R,RD,D,LD,STOP};
	private Direction dir = Direction.STOP;
	private Direction ptDir = Direction.U;//炮筒方向
	
	private static Random r = new Random();
	private int step = r.nextInt(15)+3;//电脑坦克每次移动的步数
	
	TankClient tc;//持有对方的引用
	public Tank(int x, int y,boolean good,Direction dir,TankClient tc)
	{
		this(x, y,good);
		this.dir = dir;
		this.tc = tc;
	}
	public Tank(int x, int y,boolean good)
	{
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.good = good;
	}
	
	void move()
	{
		this.oldX = x;
		this.oldY = y;
		
		switch (dir)
		{
		case L:
			x -= XSPEED;
			break;
		case LU:
			x-=XSPEED;
			y-=YSPEED;
			break;
		case U:
			y-=YSPEED;
			break;
		case RU:
			x+=XSPEED;
			y-=YSPEED;
			break;
		case R:
			x+=XSPEED;
			break;
		case RD:
			x+=XSPEED;
			y+=YSPEED;
			break;
		case D:
			y+=YSPEED;
			break;
		case LD:
			x-=XSPEED;
			y+=YSPEED;
			break;
		case STOP:
			break;
		}
		
		if (this.dir != Direction.STOP)
		{
			this.ptDir = this.dir;
		}
		
		if(x<0) x = 0;
		if(y<25) y = 25;
		if(x+Tank.WIDTH>TankClient.GAME_WIDTH) x = TankClient.GAME_WIDTH-Tank.WIDTH;
		if(y+Tank.HEIGHT>TankClient.GAME_HEIGHT) y = TankClient.GAME_HEIGHT-Tank.HEIGHT;
		
		if(!good)
		{
			Direction[] dirs = Direction.values();
			if(step==0)
			{
				step=r.nextInt(15)+3;
				int randomNum = r.nextInt(dirs.length);
				dir = dirs[randomNum];
			}
			step --;
			if(r.nextInt(40)>34) this.fire();
			
		}
	}
	
	public void draw(Graphics g)
	{
		if (!live)
		{
			if(!good)
			{
				tc.tanks.remove(this);	
			}
			return;
		}
		
		Color color = g.getColor();
		if(good) g.setColor(Color.RED);
		else g.setColor(Color.BLUE);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(color);
		
		switch (ptDir)
		{
		case L:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x-5, y+Tank.HEIGHT/2);
			break;
		case LU:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y);
			break;
		case U:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH/2, y-5);
			break;
		case RU:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y);
			break;
		case R:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH+5, y+Tank.HEIGHT/2);
			break;
		case RD:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y+Tank.HEIGHT);
			break;
		case D:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH/2, y+Tank.HEIGHT+5);
			break;
		case LD:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y+Tank.HEIGHT);
			break;
		default:
			break;
		}
		
		move();
	}
	
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		switch (key)
		{
			case KeyEvent.VK_RIGHT:
				br = true;
				break;
			case KeyEvent.VK_LEFT:
				bl = true;
				break;
			case KeyEvent.VK_UP:
				bu = true;
				break;
			case KeyEvent.VK_DOWN:
				bd = true;
				break;
		}
		locateDirection();
	}
	
	void locateDirection()
	{
		if(bl && !bu && !br && !bd) dir = Direction.L;
		else if(bl && bu && !br && !bd) dir = Direction.LU;
		else if(!bl && bu && !br && !bd) dir = Direction.U;
		else if(!bl && bu && br && !bd) dir = Direction.RU;
		else if(!bl && !bu && br && !bd) dir = Direction.R;
		else if(!bl && !bu && br && bd) dir = Direction.RD;
		else if(!bl && !bu && !br && bd) dir = Direction.D;
		else if(bl && !bu && !br && bd) dir = Direction.LD;
		else if(!bl && !bu && !br && !bu) dir = Direction.STOP;
	}

	public void keyReleased(KeyEvent e)
	{
		int key = e.getKeyCode();
		switch (key)
		{
			case KeyEvent.VK_RIGHT:
				br = false;
				break;
			case KeyEvent.VK_LEFT:
				bl = false;
				break;
			case KeyEvent.VK_UP:
				bu = false;
				break;
			case KeyEvent.VK_DOWN:
				bd = false;
				break;
			case KeyEvent.VK_CONTROL:
				fire();
				break;
			case KeyEvent.VK_S:
				superFire();
				break;
			case KeyEvent.VK_F2:
				if (!this.live)
				{
					this.live = true;
					this.setLife(3);
					tc.setScore(0);
				}
				break;
		}
		locateDirection();
	}
	
	public Missile fire(Direction dir)
	{
		if (!live) return null;
		int x = this.x +Tank.WIDTH/2-Missile.WIDTH/2;
		int y = this.y + Tank.HEIGHT/2-Missile.HEIGHT/2;
		Missile m = new Missile(x, y,good, dir,this.tc);
		tc.missiles.add(m);
		return m;
	}
	
	public void fire()
	{
		if (!live) return;
		int x = this.x +Tank.WIDTH/2-Missile.WIDTH/2;
		int y = this.y + Tank.HEIGHT/2-Missile.HEIGHT/2;
		Missile m = new Missile(x, y,good, ptDir,this.tc);
		tc.missiles.add(m);
	}
	
	
	public Rectangle getRect()
	{
		return new Rectangle (x,y,WIDTH,HEIGHT);
	}
	
	public boolean isLive()
	{
		return live;
	}
	public void setLive(boolean live)
	{
		this.live = live;
	}
	
	public boolean isGood()
	{
		return good;
	}

	/**
	 * 
	 * @param w 被撞的墙
	 * @return 撞上则返回true,否则false
	 */
	public boolean collidesWithWall(Wall w)
	{
		if (this.live && this.getRect().intersects(w.getRect()))
		{
			this.x = oldX;
			this.y = oldY;
			return true;
		}
		return false;
	}

	public void stay()//原地不动
	{
		x = oldX;
		y = oldY;
	}
	
	public boolean collidesWithTanks(List<Tank> tanks)
	{
		for(int i=0;i<tanks.size();i++)
		{
			Tank t = tanks.get(i);
			if(this!=t)
			{
				if (this.live && t.isLive() && this.getRect().intersects(t.getRect()))
				{
					//this.x = oldX;
					//this.y = oldY;
					//t.x = oldX;
					//t.y = oldY;
					
					this.stay();
					t.stay();
					return true;
				}
			}
		}
		return false;
	}
	
	public void superFire()//超级炮弹
	{
		Direction[] dirs = Direction.values();
		
		for(int i =0;i<8;i++)
		{
			tc.missiles.add(fire(dirs[i]));
		}
	}
	
	public int getLife()
	{
		return life;
	}
	public void setLife(int life)
	{
		this.life = life;
	}

}
