package app.mkiniz.poctime.shared.adapter;

import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidFactory;
import org.springframework.stereotype.Component;

@Component
public class TsidGeneratorImpl implements TsidGenerator {

    private final TsidFactory factory = TsidFactory.newInstance256();

    @Override
    public long newIdAsLong() {
        return factory.create().toLong();
    }

    @Override
    public String newIdAsString() {
        return factory.create().toString();
    }

    @Override
    public Tsid newTsid() {
        return factory.create();
    }
}
