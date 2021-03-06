package pivx.org.pivxwallet.ui.transaction_detail_activity;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pivx.org.pivxwallet.R;
import pivx.org.pivxwallet.ui.base.BaseRecyclerFragment;
import pivx.org.pivxwallet.ui.base.tools.adapter.BaseRecyclerAdapter;
import pivx.org.pivxwallet.ui.base.tools.adapter.BaseRecyclerViewHolder;
import pivx.org.pivxwallet.ui.transaction_send_activity.custom.inputs.InputHolder;
import pivx.org.pivxwallet.ui.transaction_send_activity.custom.inputs.InputWrapper;
import pivx.org.pivxwallet.ui.transaction_send_activity.custom.inputs.InputsFragment;
import pivx.org.pivxwallet.ui.transaction_send_activity.custom.outputs.OutputHolder;
import wallet.TxNotFoundException;

/**
 * Created by furszy on 8/14/17.
 */

public class InputsDetailFragment extends BaseRecyclerFragment<InputWrapper> {

    public static final String INTENT_EXTRA_UNSPENT_WRAPPERS = "unspent_wrappers";

    private Set<InputWrapper> list;
    private BaseRecyclerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            list = new HashSet<>();
            setHasOptionsMenu(true);
            Intent intent = getActivity().getIntent();
            if (intent != null) {
                if (intent.hasExtra(INTENT_EXTRA_UNSPENT_WRAPPERS)) {
                    list = (Set<InputWrapper>) intent.getSerializableExtra(INTENT_EXTRA_UNSPENT_WRAPPERS);
                    for (InputWrapper inputWrapper : list) {
                        inputWrapper.setUnspent(pivxModule.getUnspent(inputWrapper.getParentTxHash(), inputWrapper.getIndex()));
                    }
                }
            }
            setSwipeRefresh(false);
        } catch (TxNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.invalid_inputs,Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }
    }

    @Override
    protected List<InputWrapper> onLoading() {
        return new ArrayList<>(list);
    }

    @Override
    protected BaseRecyclerAdapter<InputWrapper, ? extends BaseRecyclerViewHolder> initAdapter() {
        adapter = new BaseRecyclerAdapter<InputWrapper, FragmentTxDetail.DetailOutputHolder>(getActivity()) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat();

            @Override
            protected FragmentTxDetail.DetailOutputHolder createHolder(View itemView, int type) {
                return new FragmentTxDetail.DetailOutputHolder(itemView,type);
            }

            @Override
            protected int getCardViewResource(int type) {
                return R.layout.detail_output_row;
            }

            @Override
            protected void bindHolder(final FragmentTxDetail.DetailOutputHolder holder, final InputWrapper data, int position) {
                holder.txt_num.setText("Position "+position);
                holder.txt_address.setText(data.getLabel());
                holder.txt_value.setText(data.getUnspent().getValue().toFriendlyString());
            }
        };
        return adapter;
    }
}
