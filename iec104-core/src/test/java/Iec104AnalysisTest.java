import cn.hutool.core.util.ByteUtil;
import cn.hutool.core.util.HexUtil;
import com.github.mujave.iec104.core.Iec104ParserFactory;
import com.github.mujave.iec104.core.parser.APDUParser;
import com.github.mujave.iec104.core.parser.ParserException;
import com.github.mujave.iec104.core.parser.frame.AIec104Frame;

public class Iec104AnalysisTest {

    static APDUParser parser = Iec104ParserFactory.createParser();

    public static void main(String[] args) throws ParserException {

        System.out.printf(parser.analysis(HexUtil.decodeHex("680E0000000046010400010000000001")).console());
        System.out.printf(parser.analysis(HexUtil.decodeHex("681402000A00670106000100000000e0c00f0a830310")).console());
        System.out.printf(parser.analysis(HexUtil.decodeHex("680E06000E002D01 0600010001600081")).console());
    }
}
