import cn.hutool.core.util.HexUtil;
import com.github.mujave.iec104.core.Iec104ParserFactory;
import com.github.mujave.iec104.core.parser.APDUParser;
import com.github.mujave.iec104.core.parser.ParserException;
import com.github.mujave.iec104.core.parser.frame.AIec104Frame;
import org.junit.Test;

/**
 * IEC 104 协议报文解析测试类
 * 
 * <p>该测试类包含 33 条真实的 IEC 104 报文示例，用于验证解析器对各种类型报文的解析能力：
 * <ul>
 *   <li>U帧：启动/停止数据传输、测试帧</li>
 *   <li>S帧：接收序列号确认</li>
 *   <li>I帧：各类信息对象（遥信、遥测、遥控、时钟同步等）</li>
 * </ul>
 * 
 * <p>测试覆盖的 TI 类型：
 * <ul>
 *   <li>C_IC_NA_1 (0x46): 初始化结束</li>
 *   <li>C_RP_NA_1 (0x64): 召唤命令</li>
 *   <li>C_CS_NA_1 (0x01): 时钟同步命令</li>
 *   <li>M_SP_NA_1 (0x01): 单点遥信</li>
 *   <li>M_DP_NA_1 (0x03): 双点遥信</li>
 *   <li>M_ME_NA_1 (0x0D): 归一化遥测</li>
 *   <li>M_ME_NB_1 (0x0E): 标度化遥测</li>
 *   <li>M_ME_NC_1 (0x1E): 浮点遥测</li>
 *   <li>C_SC_NA_1 (0x2D): 遥控单命令</li>
 *   <li>C_DC_NA_1 (0x67): 遥控双命令</li>
 *   <li>C_RC_NA_1 (0x69): 遥控升降命令</li>
 *   <li>C_RD_NA_1 (0x84): 读参数命令</li>
 *   <li>C_SE_NA_2 (0x88): 设置参数命令</li>
 * </ul>
 */
public class Iec104AnalysisTest {

    /** IEC 104 协议解析器实例 */
    private static final APDUParser parser = Iec104ParserFactory.createParser();

    /**
     * 测试所有报文示例
     * 
     * <p>遍历所有 33 条报文示例，逐一解析并输出结果。
     * 统计成功和失败的数量，最后输出测试摘要。
     */
    @Test
    public void testAllMessages() {
        // 测试报文数组，包含各类 IEC 104 报文
        String[] messages = {
            "680407000000",                                        // U帧: STARTDT_C (启动数据传输命令)
            "68040B000000",                                        // U帧: STARTDT_V (启动数据传输确认)
            "680E0000000046010400010000000001",                   // I帧: C_IC_NA_1 (初始化结束)
            "680E0000020064010600010000000014",                   // I帧: C_RP_NA_1 (召唤命令 - 总召唤)
            "680E0200020064010700010000000014",                   // I帧: C_RP_NA_1 (召唤命令 - 突发召唤)
            "682D0400020001A0140001000100000000000000000000000000000000000000000000000000000000000000000000", // I帧: C_CS_NA_1 (时钟同步命令)
            "681306000200098214000100014000A11000891500",         // I帧: C_CS_NA_1 (时钟同步响应)
            "680E0800020064010A00010000000014",                   // I帧: C_RP_NA_1 (召唤命令 - 复位进程)
            "680401000A00",                                        // S帧: 接收序列号确认 (SN=10)
            "681402000A00670106000100000000e0c00f0a830310",       // I帧: C_DC_NA_1 (遥控双命令 - 预置)
            "68140A00040067010700010000000001ce0f0a830310",       // I帧: C_DC_NA_1 (遥控双命令 - 执行)
            "681404000C0067010500010000000000000000000000",       // I帧: C_DC_NA_1 (遥控双命令 - 取消)
            "68140C000600670105000100000000eddc0f0a830310",       // I帧: C_DC_NA_1 (遥控双命令 - 取消)
            "680E06000E002D010600010001600081",                   // I帧: C_SC_NA_1 (遥控单命令 - 预置)
            "680E0E0008002D010700010001600081",                   // I帧: C_SC_NA_1 (遥控单命令 - 执行)
            "680E080010002D010600010001600001",                   // I帧: C_SC_NA_1 (遥控单命令 - 预置)
            "680E10000A002D010700010001600001",                   // I帧: C_SC_NA_1 (遥控单命令 - 执行)
            "680E12000A002d010a00010001600001",                   // I帧: C_SC_NA_1 (遥控单命令 - 终止)
            "680E080010002D010800010001600081",                   // I帧: C_SC_NA_1 (遥控单命令 - 预置)
            "680E10000A002D010900010001600081",                   // I帧: C_SC_NA_1 (遥控单命令 - 执行)
            "680E0A00100001010300010001000001",                   // I帧: M_SP_NA_1 (单点遥信)
            "68150C0010001E0103000100010000019ae5190a830310",     // I帧: M_ME_NC_1 (浮点遥测带时标)
            "68120E0010000D010300010002400023db2d4100",           // I帧: M_ME_NA_1 (归一化遥测)
            "680E1000100069010600010000000001",                   // I帧: C_RC_NA_1 (遥控升降命令 - 预置)
            "680E1000120069010700010000000001",                   // I帧: C_RC_NA_1 (遥控升降命令 - 执行)
            "68111200120084010600010003500000000000",             // I帧: C_RD_NA_1 (读参数命令 - 预置)
            "6811120014008401070001000350007BD44440",             // I帧: C_RD_NA_1 (读参数命令 - 执行)
            "68121400140088010600010003500058D4444080",           // I帧: C_SE_NA_2 (设置参数命令 - 预置)
            "68121400160088010700010003500058D4444080",           // I帧: C_SE_NA_2 (设置参数命令 - 执行)
            "68121600160088010600010003500058D4444000",           // I帧: C_SE_NA_2 (设置参数命令 - 预置)
            "68121600180088010700010003500058D4444000",           // I帧: C_SE_NA_2 (设置参数命令 - 执行)
            "680443000000",                                        // U帧: TESTFR_C (测试帧命令)
            "680483000000"                                         // U帧: TESTFR_V (测试帧确认)
        };

        int successCount = 0;    // 成功解析的报文数
        int failCount = 0;       // 解析失败的报文数

        System.out.println("=== IEC104 报文解析测试 ===");
        System.out.println("总报文数: " + messages.length);

        // 遍历所有报文进行解析
        for (int i = 0; i < messages.length; i++) {
            String hex = messages[i];
            System.out.println("\n--- 报文 [" + (i + 1) + "] ---");
            System.out.println("原始数据: " + hex);

            try {
                // 解析报文
                AIec104Frame frame = parser.analysis(HexUtil.decodeHex(hex));
                System.out.println("结果: SUCCESS");
                System.out.println(frame.console());
                successCount++;
            } catch (ParserException e) {
                // 解析器抛出的预期异常
                System.out.println("结果: FAIL - " + e.getMessage());
                failCount++;
            } catch (Exception e) {
                // 未预期的异常（如空指针、数组越界等）
                System.out.println("结果: ERROR - " + e.getClass().getName() + ": " + e.getMessage());
                e.printStackTrace();
                failCount++;
            }
        }

        // 输出测试摘要
        System.out.println("\n=== 测试摘要 ===");
        System.out.println("成功: " + successCount + " / " + messages.length);
        System.out.println("失败: " + failCount + " / " + messages.length);
    }
}