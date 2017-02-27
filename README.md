# KSimpleLibrary
一个简化通用APP开发过程的综合库（包含简化的Tab Bar 和Drawer 等，适用于独立开发者或者中小型应用）

>这是一个简单的面向独立 APP 制作者的库，包含了一些通用的界面元素，例如底部 TAB 栏，侧滑菜单等常用 UI ，另外，提供了通用的 Activity 及 Fragment Loading 提示，全局下拉刷新，数据库操作方面使用了 Afinal 这个三方库，网络操作封装了 LiteHttp ，缓存方面使用了 ACache，可以方便结合界面元素进行缓存加载 ，封装了 SharePreference，封装了常用的 Login 操作等。初衷是希望独立 APP 开发者能够快速实现核心功能，而不再纠结各种界面和数据操作细节。


##2015-12-13 更新

###1.KRefreshActivity&&KRefreshFragment 支持上拉加载更多了（只支持ListView）

使用方式：

1.依然是继承 KRefreshFragment 或者 KRefreshActivity。


2.在初始化完成你的ListView 后，调用一行代码：


	initLoadMoreFunc();
	
	
大功告成！

自定义显示风格和加载的逻辑


	getLoadMoreConfig()
	
	
重写以上方法即可,具体配置和下拉刷新几乎一致。详情请看示例程序中 TestListViewFragment 这个类


效果图：


![将就看吧](http://image17-c.poco.cn/mypoco/myphoto/20151213/16/17425403720151213160741024.gif?308x463_110)


http://image17-c.poco.cn/mypoco/myphoto/20151213/16/17425403720151213160741024.gif?308x463_110


###2.修复了一些BUG

## 先上个预览图吧

![将就看吧](http://image17-c.poco.cn/mypoco/myphoto/20151201/20/17425403720151201203317038.gif?333x514_110)


![将就看吧](http://image17-c.poco.cn/mypoco/myphoto/20151201/22/17425403720151201221753035.gif?336x498_110)


图片链接：http://image17-c.poco.cn/mypoco/myphoto/20151201/20/17425403720151201203317038.gif?333x514_110


图片链接：http://image17-c.poco.cn/mypoco/myphoto/20151201/22/17425403720151201221753035.gif?336x498_110


不要吐槽TabBar  ，Google也在这么干（手动滑稽）

##如何接入

import ksimplelibrary 这个 module

编写自己的Application，继承 KSimpleApplication


##Activty 和 Fragment

普通的Activity 可继承 KSimpleBaseActivityImpl 并实现 IBaseAction 接口

Fragment 一样，继承 KSimpleBaseFragmentImpl 并实现 IBaseAction 接口

有啥用？

#### 初始化过程更清晰
将初始化过程分为

int initLocalData()	 		初始化本地数据，可直接在此访问数据库

initView()					初始化 View 控件信息

initController()			初始化控制器，即添加逻辑代码
	
onLoadingNetworkData()		加载网络数据,可直接在此访问网络

onLoadedNetworkData(View content)		加载完毕,更新 View


#### 管理数据更加便捷

在页面加载时（initLocalData、onLoadingNetworkData）时会自动调出Loading 界面，你可以以返回值的形式定义在加载完本地数据后是否应该继续显示Loading 界面，可以由以下常量定义：默认值为0


 	/**
     * 需要继续加载网络数据并继续loading
     */
    int LOAD_NETWORK_DATA_AND_SHOW = 0;

    /**
     * 取消loading，在后台加载网络数据
     */
    int LOAD_NETWORK_DATA_AND_DISMISS = 1;

    /**
     * 不加载网络数据
     */
    int DONT_LOAD_NETWORK_DATA = 2;
    

并且自带了缓存本地数据功能，例如：你可以在onLoadedNetworkData 网络数据加载完毕后 函数体中调用 cachePageData(HashMap<String, Object> dataMap）缓存你想要缓存的本地数据，又在initLocalData 中调用 getCachePageData 得到之前缓存的数据，以判断是否该继续显示 loading 界面

#### 任务管理
使用SimpleTaskManager 时能够在 Activity 的生命周期结束后取消该Activity 发出的Task

##简单的任务管理

####一般后台任务

需要进行后台任务时，调用：


	SimpleTaskManager.startNewTask(new SimpleTask(getTaskTag()) {

            @Override
            protected Object doInBackground(Object[] params) {
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
               
            }
        });
        
任务默认会在Activity 或者Fragment 生命周期结束后取消，也可以通过设定cancelFlag 自定义

#### 网络后台任务

通过NetworkTask 可以快速创建一个网络后台任务，例如：


	SimpleTaskManager.startNewTask(new NetworkTask(getTaskTag(),getApplicationContext(),MyResponse.class,paramsHashMap,GET){
            @Override
            public void onExecutedMission(NetworkExecutor.NetworkResult result) {
                
            }

            @Override
            public void onExecutedFailed(NetworkExecutor.NetworkResult result) {

            }
    });
    
    
以上面的代码为例：
NetworkExecutor.NetworkResult result 是网络任务的返回对象

result.resultObject 是经过Gson 转换后的 MyResponse.class 的实例对象
result.tips 是网络失败后的原因提示

#### 如何实现自动登录

#####1.编写网络数据Model XXX 继承自 BaseResponse

例如：


	public class LoginResponse extends BaseResponse {
	    private int responseCode;
	    private String token;
	    private Student student;
    }
    
    
服务器返回的样例JSON：


	{
	  "responseCode":202,
	  "responseString":"登录成功",
	  "student":
	    {
	      "name":"kot32",
	      "age":21,
	      "sex":"male",
	      "username":"kot32",
	      "password":"12345678"
	    }
	}


#####2.编写用户数据Model XXX 继承自 BaseUserModel

例如：



	public class Student extends BaseUserModel {
    	private int id;
    	private String username;
    	private String password;
	}
	
	
	

#####3.实现LoginTask

在Application 继承 KSimpleApplication 时，实现 getLoginTask 方法 ，让程序能够在登陆成功后保存用户信息在内存及本地缓存
例如：


	@Override
    public LoginTask getLoginTask() {
        return new LoginTask(getTaskTag(), this,
                LoginResponse.class, loginParams, SERVER_URL, NetworkTask.GET) {
            @Override
            public boolean isLoginSucceed(BaseResponse baseResponse) {
                LoginResponse loginResponse = (LoginResponse) baseResponse;
                if (loginResponse.getResponseCode() == 202) {
                    return true;
                }
                return false;
            }

            @Override
            public void onConnectFailed(NetworkExecutor.NetworkResult result) {

            }
        };
    }

如果在 isLoginSucceed 中返回了true，程序会对UserModel进行自动缓存


####注销

KSimpleApplication 中的 logout 可以实现注销
    
    
## 全局下拉刷新使用


Activity ：继承 KRefreshActivity 并实现 IBaseAction

Fragment：继承  KRefreshFragment 并实现 IBaseAction

注意：如果Activity 或者Fragment 布局为：


	 <RelativeLayout>
		<WebView>
		</WebView>
	</RelativeLayout>
	
如果想对 WebView 进行下拉刷新，请去掉外层的 RelativeLayout

更多自定义下拉刷新的内容，参见：https://github.com/kot32go/RefreshView  或者样例代码


## TabBar 使用

写一个Activity 继承 KTabActivity 并实现 IBaseAction 接口，以下是一个例子（有ToolBar），具体可见项目中的示例代码

STYLE_GRADUAL 代表微信的TabBar样式（滑动+渐变）
NORMAL 代表普通点击切换的样式


	@Override
    public void initController() {
        addTab(R.mipmap.chats, R.mipmap.chats_green, "聊天", Color.GRAY, Color.parseColor("#04b00f"));
        addTab(R.mipmap.contacts, R.mipmap.contacts_green, "联系人", Color.GRAY, Color.parseColor("#04b00f"));
        addTab(R.mipmap.discover, R.mipmap.discover_green, "发现", Color.GRAY, Color.parseColor("#04b00f"));
        addTab(R.mipmap.about_me, R.mipmap.about_me_green, "我", Color.GRAY, Color.parseColor("#04b00f"));
    }
    //进行详细设定，可以通过TabConfig 指定TabBar 占全局的高度，以及每一个Tab 中的图标所占的比例
    @Override
    public KTabActivity.TabConfig getTabConfig() {
        return null;
    }

    @Override
    public List<Fragment> getFragmentList() {
        fragmentList.add(new TestFragment());
        fragmentList.add(new TestFragment());
        fragmentList.add(new TestFragment());
        fragmentList.add(new TestFragment());
        return fragmentList;
    }

    @Override
    public View getCustomContentView(View v) {
        ViewGroup vg = (ViewGroup) super.getCustomContentView(v);
        toolbar = (Toolbar) getLayoutInflater().inflate(R.layout.default_toolbar, null);
        vg.addView(toolbar, 0);
        return vg;
    }
    
    
##Drawer 的使用

你有两种很简单的方式来定义侧滑菜单Drawer

###1.通过KDrawerBuilder 去构建


	
	DrawerLayout drawer = new KDrawerBuilder(this)
                .withToolBar(toolbar)
                .withWidth(300)
                .addDrawerHeader(header, null)
                .addDrawerSectionTitle("菜单", Color.DKGRAY)
                .addDrawerSubItem(R.drawable.ic_collected, "收藏", null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "点击了收藏", Toast.LENGTH_SHORT).show();
                    }
                })
                .addDrawerSubItem(R.drawable.ic_commented, "评论", null, null)
                .addDrawerSubItem(R.drawable.ic_drawer_explore_normal, "探索", null, null)
                .addDrawerSubItem(R.drawable.ic_register_normal, "注册", null, null)
                .addDrawerDivider(Color.parseColor("#f1f2f1"))
                .addDrawerSubItem("", "关于", null, null)
                .addDrawerSubItem("", "更多设置", null, null)
                .withDrawerAction(new KDrawerBuilder.DrawerAction() {
                    @Override
                    public void onDrawerOpened(View kDrawerView) {
                        //打开了侧滑菜单
                        if (getSimpleApplicationContext().isLogined()) {
                            Student student = (Student) getSimpleApplicationContext().getUserModel();
                            header.changeNickName(student.getUsername());
                            header.changeIntroduction("保持饥饿，保持愚蠢");
                        }
                    }

                    @Override
                    public void onDrawerClosed(View kDrawerView) {
                        //关闭了侧滑菜单
                    }
                })
                .build();
                
                
 Drawer 的头部是可以自定义的
 
 
 	DrawerComponent.DrawerHeader header = new DrawerComponent.DrawerHeader(DrawerComponent.DrawerHeader.DrawerHeaderStyle.NORMAL,
                R.drawable.drawer_theme_6_bg,
                this);
        header.addNickName("未登录");
        header.addIntroduction("请点击默认头像登录");
        header.addAvatar(R.drawable.avatar, AVATAR_URL, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断当前是否登录
                if (getSimpleApplicationContext().isLogined()) {
                    //do somehting
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    drawer.closeDrawers();
                }
            }
        });
        
  
  
  DrawerHeaderStyle.NORMAL    代表普通背景图模式
  DrawerHeaderStyle.KENBURNS  代表平移渐变背景图模式，效果如下(用了MaterialViewPager 中的代码)：
  
   
 ![将就看吧](http://image17-c.poco.cn/mypoco/myphoto/20151201/21/17425403720151201215827064.gif?336x498_110
)


图片链接：http://image17-c.poco.cn/mypoco/myphoto/20151201/21/17425403720151201215827064.gif?336x498_110



###2.通过继承KDrawerContentLayout 用xml自己写布局实现


	new KDrawerBuilder(this)
              .withToolBar(toolbar)
              .withCustomContentView(new DrawerMenu(this))
              .withWidth(300)
	          .build();
	          
	          
其中 DrawerMenu 继承自 KDrawerContentLayout


###3.如何复用Drawer？

可能在数个Activity中都要存在Drawer，这时可以用 new KDrawerBuilder(otherActivity.this).withExistedDrawer(drawer)...



##简化ListView 和 RecyclerView 的Adapter的创建

###ListView

Adapter 继承自 SimpleBaseAdapter<T>（取自StormZhang的博客）

例如：


	public class TestFoodListAdapter extends SimpleBaseAdapter<String> {

	    public TestFoodListAdapter(Context context, List<String> data) {
	        super(context, data);
	    }

	    @Override
	    public int getItemResource() {
	        return R.layout.listitem_test;
	    }

	    @Override
	    public View getItemView(int position, View convertView, ViewHolder holder) {
	        TextView text = holder.getView(R.id.text);
	        text.setText(getItem(position));
	        return convertView;
	    }
	}



###RecyclerView

Adapter 继承自 SimpleRecycleAdapter<T> 

参见：https://github.com/kot32go/SimpleRecycleAdapter




暂时就这些，第一次撸库，目的是解决独立开发者会遇到的『怎么又是这些功能啊』的问题，大神轻喷
 
        


