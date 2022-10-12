package indi.dbfmp.ttsocket.protocol;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.StrUtil;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class CsvTest {

    @Test
    public void vsTest() {
        CsvReader reader = CsvUtil.getReader();
        //从文件中读取CSV数据
        CsvData data = reader.read(FileUtil.file("C:\\Users\\Administrator\\Desktop\\three.csv"));
        List<CsvRow> rows = data.getRows();
        //遍历行
        for (CsvRow csvRow : rows) {
            //getRawList返回一个List列表，列表的每一项为CSV中的一个单元格（既逗号分隔部分）
            List<String> rawList = csvRow.getRawList();
            String splitUrl = rawList.get(3).replace("/app/ess/contractDownload/", "").split("/")[1];
            String newName = StrUtil.join("-",rawList.get(4),rawList.get(0),rawList.get(1),splitUrl);
            FileUtil.copy(new File("C:\\Users\\Administrator\\Desktop\\下载合同\\all\\"+splitUrl),
                    new File("C:\\Users\\Administrator\\Desktop\\下载合同\\allRename\\"+newName),true);
            Console.log(csvRow.getRawList());
        }
    }


}
