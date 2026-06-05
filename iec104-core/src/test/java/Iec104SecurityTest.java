import cn.hutool.core.util.HexUtil;
import com.github.mujave.iec104.core.Iec104ParserFactory;
import com.github.mujave.iec104.core.parser.APDUParser;
import com.github.mujave.iec104.core.parser.ParserException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * IEC 104 协议安全测试类
 * 
 * <p>该测试类用于验证解析器对异常报文的处理能力，确保解析器具备良好的安全性：
 * <ul>
 *   <li>拒绝服务攻击防护（短报文、越界报文）</li>
 *   <li>无效数据校验（无效TI、无效COT）</li>
 *   <li>正常报文解析能力验证</li>
 * </ul>
 */
public class Iec104SecurityTest {

    /** IEC 104 协议解析器实例 */
    private final APDUParser parser = Iec104ParserFactory.createParser();

    /**
     * 测试短报文（长度不足）
     * 
     * <p>验证解析器能够正确处理长度不足的报文，防止数组越界异常。
     * 合法的 APDU 报文至少需要 6 字节（固定头部）。
     */
    @Test
    public void testShortMessage() {
        System.out.println("测试短报文（长度不足）");
        try {
            // 发送一个只有 5 字节的非法报文
            parser.analysis(HexUtil.decodeHex("680300000000"));
            fail("未捕获到预期异常");
        } catch (ParserException e) {
            System.out.println("PASS: 正确捕获异常 - " + e.getMessage());
        }
    }

    /**
     * 测试越界报文（PoC案例）
     * 
     * <p>验证解析器能够正确处理恶意构造的越界报文，防止缓冲区溢出攻击。
     * 此测试确保解析器在处理声称包含大量信息元素但实际数据不足的报文时不会崩溃。
     */
    @Test
    public void testOverflowMessage() {
        System.out.println("测试越界报文（PoC案例）");
        try {
            // 恶意构造的越界报文，声称包含大量信息元素但实际数据不足
            String hex = "681000000000016400000001000000010000";
            parser.analysis(HexUtil.decodeHex(hex));
            fail("未捕获到预期异常");
        } catch (ParserException e) {
            System.out.println("PASS: 正确捕获异常 - " + e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            fail("仍存在越界异常 - " + e.getMessage());
        }
    }

    /**
     * 测试无效类型标识符TI
     * 
     * <p>验证解析器能够正确处理未知的类型标识符（TI=255），确保不会因为
     * 无法识别的 TI 值而导致解析失败或抛出未预期的异常。
     */
    @Test
    public void testInvalidTI() {
        System.out.println("测试无效类型标识符TI");
        try {
            // TI=FF (255) 是未定义的类型标识符
            String hex = "680E00000000FF010400010000000001";
            parser.analysis(HexUtil.decodeHex(hex));
            fail("未捕获到预期异常");
        } catch (ParserException e) {
            System.out.println("PASS: 正确捕获异常 - " + e.getMessage());
        }
    }

    /**
     * 测试无效传送原因COT
     * 
     * <p>验证解析器能够正确处理无效的传送原因（COT=63），确保在解析过程中
     * 能够正确识别并拒绝无效的传送原因值。
     */
    @Test
    public void testInvalidCOT() {
        System.out.println("测试无效传送原因COT");
        try {
            // COT=FF (255) 是无效的传送原因
            String hex = "680E000000000101FF00010000000001";
            parser.analysis(HexUtil.decodeHex(hex));
            fail("未捕获到预期异常");
        } catch (ParserException e) {
            System.out.println("PASS: 正确捕获异常 - " + e.getMessage());
        }
    }

    /**
     * 测试有效报文
     * 
     * <p>验证解析器能够正确解析合法的 IEC 104 报文，确保正常功能不受影响。
     * 使用初始化结束报文（C_IC_NA_1, TI=0x46）进行测试。
     */
    @Test
    public void testValidMessage() {
        System.out.println("测试有效报文");
        try {
            // 合法的初始化结束报文
            parser.analysis(HexUtil.decodeHex("680E0000000046010400010000000001"));
            System.out.println("PASS: 解析成功");
        } catch (ParserException e) {
            fail("错误捕获异常 - " + e.getMessage());
        }
    }
}