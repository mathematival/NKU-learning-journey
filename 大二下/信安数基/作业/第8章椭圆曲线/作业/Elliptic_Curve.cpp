#include<iostream>
using namespace std;

#define Elliptic_Curve_EC "E_" << p << "(" << a << ',' << b << ")"
#define Point_P "P(" << x << "," << y << ")"

// 类声明
class Point
{
public:
	int x, y;
	bool isINF; //是否是无穷远点
	Point(int x = 0, int y = 0, bool isINF = false); // 构造函数
	friend ostream& operator<< (ostream& out, const Point& p); // 输出运算符重载
	void output(ostream& out) const; // 输出函数
};

// 类定义
Point::Point(int x, int y, bool isINF)
	: x(x), y(y), isINF(isINF) {}

void Point::output(ostream& out) const
{
	if (isINF) out << 'O'; // 无穷远点
	else out << '(' << x << ',' << y << ')'; // 非无穷远点
}

ostream& operator<< (ostream& out, const Point& p)
{
	p.output(out);
	return out;
}

// 椭圆曲线类声明
class Elliptic_Curve
{
private:
	int p, a, b;
	int powMod(int x, int n, int mod); // 快速幂算法
	int get_Inverse(int a, int m); // 在 (a, m) = 1 的条件下，求a模m的乘法逆元
	int Legendre(int a, int p); // 计算勒让德符号
public:
	Elliptic_Curve(int p, int a, int b);
	bool is_inverse(const Point& p1, const Point& p2); //判断两个点是否互逆
	bool is_Elliptic_Curve(); //检查当前参数是否能构成椭圆曲线
	bool is_on_Elliptic_Curve(const Point& p); //判断p点是否在椭圆曲线上
	Point addPoints(const Point& p1, const Point& p2); //进行点加运算
	Point multiplyPoint(Point p, int k); //对点p进行倍加
	Point inversePoint(const Point& pt); // 添加计算点的逆元的方法
	int ord_of_Point(const Point& p); //计算点p的阶
	int ord_of_Elliptic_Curve(); //计算此椭圆曲线的阶#E
	int show_all_Points(); //展示出椭圆曲线上的所有点
};

// 椭圆曲线类定义
int Elliptic_Curve::powMod(int x, int n, int mod) // 快速幂算法
{
	int c = 1;
	while (n) {
		if (n & 1) {
			c = (c * x) % mod;
		}
		x = (x * x) % mod;
		n >>= 1;
	}
	return c;
}

int Elliptic_Curve::get_Inverse(int a, int m) //在 (a, m) = 1 的条件下，求a模m的乘法逆元
{
	a = (a + m) % m;
	int s0 = 1, s1 = 0;
	int r0 = a, r1 = m;
	while (r1 != 0)
	{
		int q = r0 / r1;
		int tmp = r1;
		r1 = r0 % r1;
		r0 = tmp;

		tmp = s1;
		s1 = s0 - s1 * q;
		s0 = tmp;
	}
	return (s0 + m) % m;
}

int Elliptic_Curve::Legendre(int a, int p) //p是奇素数, (a, p) = 1
{
	if (a < 0)
	{
		if (a == -1)
		{
			return p % 4 == 1 ? 1 : -1;
		}
		return Legendre(-1, p) * Legendre(-a, p);
	}
	a %= p;
	if (a == 1)
	{
		return 1;
	}
	else if (a == 2)
	{
		if (p % 8 == 1 || p % 8 == 7) return 1;
		else return -1;
	}
	// 下面将a进行素数分解
	int ret = 1;
	while (a > 1)
	{
		int prime = 2;
		while (a % prime == 0)
		{
			a /= prime;
			if (prime == 2)
			{
				if (p % 8 == 3 || p % 8 == 5) ret = -ret;
			}
			else
			{
				if (((prime - 1) * (p - 1) / 4) % 2 == 1) ret = -ret;
				ret *= Legendre(p % prime, prime);
			}
		}
		if (a == 1) break;
		if (a % 4 == 3 && p % 4 == 3) ret = -ret;
		swap(a, p);
		a %= p;
	}
	return ret;
}

Elliptic_Curve::Elliptic_Curve(int p, int a, int b) //椭圆曲线构造函数
	: p(p), a(a), b(b) {}

bool Elliptic_Curve::is_inverse(const Point& p1, const Point& p2)
{
	return (p1.x - p2.x) % p == 0 && (p1.y + p2.y) % p == 0;
}

bool Elliptic_Curve::is_Elliptic_Curve() //检查当前参数是否能构成椭圆曲线
{
	return (4 * powMod(a, 3, p) + 27 * powMod(b, 2, p)) % p != 0; // 椭圆曲线条件判断
}

bool Elliptic_Curve::is_on_Elliptic_Curve(const Point& pt)// 判断点是否在曲线上
{
	if (pt.isINF) return true;
	return (powMod(pt.y, 2, p) - powMod(pt.x, 3, p) - a * pt.x - b) % p == 0;
}

Point Elliptic_Curve::addPoints(const Point& p1, const Point& p2)// 计算两点之和
{
	if (p1.isINF) return p2;
	if (p2.isINF) return p1;
	if (is_inverse(p1, p2)) return { 0, 0, true };
	if ((p1.x - p2.x) % p == 0) //倍加公式
	{
		int k = ((3 * powMod(p1.x, 2, p) + a) * get_Inverse(2 * p1.y, p) % p + p) % p;
		int x3 = ((powMod(k, 2, p) - 2 * p1.x) % p + p) % p;
		int y3 = ((k * (p1.x - x3) - p1.y) % p + p) % p;
		return { x3, y3 };
	}
	else                        //点加公式
	{
		int k = ((p2.y - p1.y) * get_Inverse(p2.x - p1.x, p) % p + p) % p;
		int x3 = ((powMod(k, 2, p) - p1.x - p2.x) % p + p) % p;
		int y3 = ((k * (p1.x - x3) - p1.y) % p + p) % p;
		return { x3, y3 };
	}
}

Point Elliptic_Curve::multiplyPoint(Point pt, int k)// 使用倍加-和算法计算kP
{
	Point ret(0, 0, true);
	while (k)
	{
		if (k & 1)
		{
			ret = addPoints(ret, pt);
		}
		pt = addPoints(pt, pt);
		k >>= 1;
	}
	return ret;
}

Point Elliptic_Curve::inversePoint(const Point& pt) {
	if (pt.isINF) return pt;
	return Point(pt.x, (p - pt.y) % p);
}

int Elliptic_Curve::ord_of_Point(const Point& pt)// 计算点的阶
{
	if (!is_on_Elliptic_Curve(pt)) return -1;// 点不在椭圆曲线上，返回-1
	int ord = 1;
	Point tmp = pt;
	while (!tmp.isINF)
	{
		tmp = addPoints(tmp, pt);
		++ord;
	}
	return ord;
}

int Elliptic_Curve::ord_of_Elliptic_Curve()
{
	int ord = 1;
	for (int x = 0; x < p; ++x)
	{
		int tmp = (x * x * x + a * x + b + p) % p;
		if (tmp == 0)
		{
			ord += 1;
		}
		else if (Legendre(tmp, p) == 1)
		{
			ord += 2;
		}
	}
	return ord;
}

int Elliptic_Curve::show_all_Points()
{
	cout << "O ";
	int sum = 1;
	for (int x = 0; x < p; ++x)
	{
		int tmp = (x * x * x + a * x + b + p) % p;
		if (tmp == 0)
		{
			cout << " (" << x << ',' << "0) ";
			sum++;
		}
		else if (Legendre(tmp, p) == 1) //贡献两个点
		{
			for (int y = 1; y < p; ++y) //从1遍历到p-1，寻找解
			{
				if ((y * y - tmp) % p == 0)
				{
					cout << " (" << x << ',' << y << ") ";
					sum++;
					cout << " (" << x << ',' << p - y << ") ";
					sum++;
					break;
				}
			}
		}
	}
	cout << endl;
	return sum;
}

int main()
{
	cout << "Z_P上的椭圆曲线E_p(a, b)的计算" << endl;
	cout << "————————————————————————————————————————" << endl;
	cout << endl;

	//功能一：给定参数p，a，b，判断E_p(a,b)是否为椭圆曲线
	cout << "1.判断所给参数是否能构成一个椭圆曲线" << endl;
	int p, a, b;
	cout << "请输入椭圆曲线的参数 p: ";
	cin >> p;
	cout << "请输入椭圆曲线的参数 a: ";
	cin >> a;
	cout << "请输入椭圆曲线的参数 b: ";
	cin >> b;
	Elliptic_Curve ec(p, a, b);
	cout << Elliptic_Curve_EC << " is ";
	if (!ec.is_Elliptic_Curve())
	{
		cout << "not ";
		system("pause");
		return 0;
	}
	cout << "Elliptic_Curve" << endl;
	cout << endl;

	//功能二：判断给定的点P，Q是否在椭圆曲线E_p(a,b)上
	cout << "2.判断给出的点是否在给定的椭圆曲线上" << endl;
	int x, y;
	cout << "输入 x: ";
	cin >> x;
	cout << "输入 y: ";
	cin >> y;
	cout << Point_P " is ";
	if (!ec.is_on_Elliptic_Curve(Point(x, y))) cout << "not ";
	cout << "on " << Elliptic_Curve_EC << endl;
	cout << endl;

	//功能三：对于椭圆曲线E_p(a,b)上的两点P,Q，计算P+Q
	cout << "3.计算给定的两点相加" << endl;
	int x1, y1, x2, y2;
	cout << "输入 x1: ";
	cin >> x1;
	cout << "输入 y1: ";
	cin >> y1;
	cout << "输入 x2: ";
	cin >> x2;
	cout << "输入 y2: ";
	cin >> y2;
	cout << "(" << x1 << "," << y1 << ")" << " + " << "(" << x2 << "," << y2 << ") = " << ec.addPoints({ x1, y1 }, { x2, y2 }) << endl;
	cout << endl;

	//功能四：对于椭圆曲线E_p(a,b)上的点P，使用倍加-和算法计算mP
	cout << "4.计算给出的点的倍加" << endl;
	cout << "输入 x: ";
	cin >> x;
	cout << "输入 y: ";
	cin >> y;
	int m;
	cout << "输入倍数: ";
	cin >> m;
	cout << m << Point_P << " = " << ec.multiplyPoint({ x, y }, m) << endl;
	cout << endl;

	//功能五：对于椭圆曲线E_p(a,b)上的点P，计算阶ord（P）
	cout << "5.计算给出的点的阶" << endl;
	cout << "输入 x: ";
	cin >> x;
	cout << "输入 y: ";
	cin >> y;
	int ord = ec.ord_of_Point({ x, y });
	if (ord != -1)
	{
		cout << Point_P << "的阶是" << ord << endl;
	}
	else {
		cout << Point_P << " is not on " << Elliptic_Curve_EC << endl;
	}
	cout << endl;

	//功能六：对于椭圆曲线E_p(a,b)，计算阶#E
	cout << "6.计算给出的椭圆曲线的阶" << endl;
	cout << Elliptic_Curve_EC << "的阶是" << ec.ord_of_Elliptic_Curve() << endl;
	cout << endl;

	//功能七：对于椭圆曲线E_p(a,b)，计算所有点
	cout << "7.列出给出的椭圆曲线上的所有点" << endl;
	ec.show_all_Points();
	cout << endl;

	// 新增功能：计算椭圆曲线上的点的逆元
	cout << "8. 计算给定点的逆元" << endl;
	cout << "输入 x: ";
	cin >> x;
	cout << "输入 y: ";
	cin >> y;
	cout << Point_P << " 的逆元是 " << ec.inversePoint({ x,y }) << endl;
	system("pause");
	return 0;
}