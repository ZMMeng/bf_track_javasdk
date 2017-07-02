import com.beifeng.ae.sdk.AnalyticsEngineSDK;

/**
 * Created by Administrator on 2017/6/29.
 */
public class Test {

    public static void main(String[] args){
        AnalyticsEngineSDK.onChargeSuccess("orderId123", "memberId123");
        AnalyticsEngineSDK.onChargeRefund("orderId456", "memberId456");
    }
}
