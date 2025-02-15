#include <bitset>
#include <iostream>
#include <string>
#include <vector>
using namespace std;

// SPN类定义
class SPN {
private:
    int l, m, Nr;               // 每组比特长度、组数、加密轮数
    vector<string> SboxBin;     // S-Box的二进制字符串表示
    vector<int> Pbox;           // P-Box
    vector<string> keys;        // 轮密钥

    // 异或操作
    void xorStrings(string& w, const string& K) {
        for (size_t i = 0; i < w.length(); ++i) {
            w[i] = (w[i] == K[i] ? '0' : '1');
        }
    }

    // S-Box代换
    void applySbox(string& u) const {
        string v(l * m, '0');
        for (int i = 0; i < m; ++i) {
            string ui = u.substr(i * l, l);
            int index = stoi(ui, nullptr, 2);   // 转换为整数索引
            v.replace(i * l, l, SboxBin[index]); // 直接替换为预先计算的二进制字符串
        }
        u = v;
    }

    // P-Box置换
    void applyPbox(string& v) {
        string w(v.length(), '0');
        for (size_t i = 0; i < v.length(); ++i) {
            w[Pbox[i] - 1] = v[i];
        }
        v = w;
    }

public:
    // 构造函数
    SPN(int l, int m, int Nr, const vector<int>& Sbox, const vector<int>& Pbox)
        : l(l), m(m), Nr(Nr), Pbox(Pbox) {
        // 预处理S-Box，将其存为二进制字符串以提高效率
        for (int val : Sbox) {
            SboxBin.push_back(bitset<4>(val).to_string());
        }
    }

    // 设置密钥
    void setKey(const string& key) {
        for (int i = 0; i <= Nr; ++i) {
            keys.push_back(key.substr(i * Nr, l * m));
        }
    }

    // SPN加密算法
    string encrypt(string plain) {
        // 前Nr-1轮
        for (int r = 0; r < Nr - 1; ++r) {
            xorStrings(plain, keys[r]);    // 异或操作
            applySbox(plain);              // S-Box代换
            applyPbox(plain);              // P-Box置换
        }

        // 最后一轮
        xorStrings(plain, keys[Nr - 1]);
        applySbox(plain);
        xorStrings(plain, keys[Nr]);

        return plain;
    }
};

int main() {
    // 参数设置
    int l = 4, m = 4, Nr = 4;
    vector<int> Sbox = {
        0xE, 0x4, 0xD, 0x1, 0x2, 0xF, 0xB, 0x8, 0x3, 0xA, 0x6, 0xC, 0x5, 0x9, 0x0, 0x7
    };
    vector<int> Pbox = {
        1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15, 4, 8, 12, 16
    };

    // 创建SPN实例
    SPN spn(l, m, Nr, Sbox, Pbox);

    // 输入明文和密钥
    string plain, key;
    cin >> plain >> key;

    // 设置密钥并加密
    spn.setKey(key);
    string cipher = spn.encrypt(plain);

    // 输出加密结果
    cout << cipher;

    return 0;
}