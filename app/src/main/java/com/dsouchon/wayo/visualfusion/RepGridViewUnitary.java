package com.dsouchon.wayo.visualfusion;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RepGridViewUnitary extends  ArrayAdapter <AndroidStoreUnit> {

    Context context;
    private ArrayList<AndroidStoreUnit> data = new ArrayList<AndroidStoreUnit>();



    public RepGridViewUnitary(Context context, ArrayList<AndroidStoreUnit> data )
    {
        super(context, 0);
        this.context=context;
        this.data = data;
    }

    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.rep_row_unitary, parent, false);
        }
        else{
            row = (View) convertView;
        }




            TextView ItemTypeName = (TextView) row.findViewById(R.id.textView);
            ImageView ImageBase64 = (ImageView) row.findViewById(R.id.imageView);
            TextView MaxQuantityFromMatrix = (TextView) row.findViewById(R.id.textViewsub);

            //TextView textUnitsSet = (TextView) row.findViewById(R.id.amount);

            AndroidStoreUnit item = data.get(position);


            ItemTypeName.setText(item.getItemTypeName());//"Display Item 1002");
            ImageBase64.setImageResource(getImageFromDrawables(item.getImagePath()));//R.drawable.dis1);//TODO

            Integer max = item.getMaxQuantityFromMatrix();
            MaxQuantityFromMatrix.setText(max.toString());
            //textUnitsSet.setText("2");




        return row;

    }

    public int getImageFromDrawables(String imageName)
    {
        int returnThis = 0;
        switch(imageName)
        {
            case "back_of_bar_essentials_tusker.png" : returnThis = R.drawable.back_of_bar_essentials_tusker; break;
            case "back_of_bar_johnnie_walker.png" : returnThis = R.drawable.back_of_bar_johnnie_walker; break;
            case "back_of_bar_tier_1_tusker.png" : returnThis = R.drawable.back_of_bar_tier_1_tusker; break;
            case "back_of_bar_tier_2_tusker.png" : returnThis = R.drawable.back_of_bar_tier_2_tusker; break;
            case "bar_front_johnnie_walker.png" : returnThis = R.drawable.bar_front_johnnie_walker; break;
            case "bench_1_guinness.png" : returnThis = R.drawable.bench_1_guinness; break;
            case "bench_2_guinness.png" : returnThis = R.drawable.bench_2_guinness; break;
            case "bench_kenya_cane.png" : returnThis = R.drawable.bench_kenya_cane; break;
            case "brand_pillars_johnnie_walker.png" : returnThis = R.drawable.brand_pillars_johnnie_walker; break;
            case "brand_pillars_tusker.png" : returnThis = R.drawable.brand_pillars_tusker; break;
            case "cafe_table_with_seating_kenya_cane.png" : returnThis = R.drawable.cafe_table_with_seating_kenya_cane; break;
            case "cocktail_table_and_chairs_smirnoff.png" : returnThis = R.drawable.cocktail_table_and_chairs_smirnoff; break;
            case "cocktail_table_with_bar_stool_kenya_cane.png" : returnThis = R.drawable.cocktail_table_with_bar_stool_kenya_cane; break;
            case "communal_bench_tusker.png" : returnThis = R.drawable.communal_bench_tusker; break;
            case "conversation_table_smirnoff.png" : returnThis = R.drawable.conversation_table_smirnoff; break;
            case "conversation_table_tusker.png" : returnThis = R.drawable.conversation_table_tusker; break;
            case "couch_guinness.png" : returnThis = R.drawable.couch_guinness; break;
            case "couch_johnnie_walker.png" : returnThis = R.drawable.couch_johnnie_walker; break;
            case "decorative_frames_johnnie_walker.png" : returnThis = R.drawable.decorative_frames_johnnie_walker; break;
            case "dj_booth_cladding_smirnoff.png" : returnThis = R.drawable.dj_booth_cladding_smirnoff; break;
            case "double_sided_illuminated_sign_smirnoff.png" : returnThis = R.drawable.double_sided_illuminated_sign_smirnoff; break;
            case "front_bar_signage_guinness.png" : returnThis = R.drawable.front_bar_signage_guinness; break;
            // case "guinnessbarfrontvinyl_logotier1.png" : returnThis = R.drawable.guinnessbarfrontvinyl_logotier1; break;
            // case "guinnessbarfrontvinyl_logotier2.png" : returnThis = R.drawable.guinnessbarfrontvinyl_logotier2; break;
            // case "guinnessbarfrontvinyl_nologotier1.png" : returnThis = R.drawable.guinnessbarfrontvinyl_nologotier1; break;
            // case "guinnessbarfrontvinyl_nologotier2.png" : returnThis = R.drawable.guinnessbarfrontvinyl_nologotier2; break;
            // case "guinnessguinnesspatternvinyltier1.png" : returnThis = R.drawable.guinnessguinnesspatternvinyltier1; break;
            // case "guinnessguinnesspatternvinyltier2.png" : returnThis = R.drawable.guinnessguinnesspatternvinyltier2; break;
            // case "guinnesspaintingessentials.png" : returnThis = R.drawable.guinnesspaintingessentials; break;
            // case "guinnesspaintingtier1tier1.png" : returnThis = R.drawable.guinnesspaintingtier1tier1; break;
            // case "guinnesspaintingtier2.png" : returnThis = R.drawable.guinnesspaintingtier2; break;
            // case "guinnesswallgraphicprintedtier1.png" : returnThis = R.drawable.guinnesswallgraphicprintedtier1; break;
            // case "guinnesswallgraphicprintedtier2.png" : returnThis = R.drawable.guinnesswallgraphicprintedtier2; break;
            case "illuminated_modrail_tusker.png" : returnThis = R.drawable.illuminated_modrail_tusker; break;
            case "indoor_coffee_table_johnnie_walker.png" : returnThis = R.drawable.indoor_coffee_table_johnnie_walker; break;
            //case "kenyacanebarfrontvinyl_logotier1.png" : returnThis = R.drawable.kenyacanebarfrontvinyl_logotier1; break;
            //case "kenyacanebarfrontvinyl_logotier2.png" : returnThis = R.drawable.kenyacanebarfrontvinyl_logotier2; break;
            //case "kenyacanebarfrontvinyl_nologotier1.png" : returnThis = R.drawable.kenyacanebarfrontvinyl_nologotier1; break;
            //case "kenyacanebarfrontvinyl_nologotier2.png" : returnThis = R.drawable.kenyacanebarfrontvinyl_nologotier2; break;
            //case "kenyacanepaintingessentials.png" : returnThis = R.drawable.kenyacanepaintingessentials; break;
            //case "kenyacanepaintingtier1.png" : returnThis = R.drawable.kenyacanepaintingtier1; break;
            //case "kenyacanepaintingtier2.png" : returnThis = R.drawable.kenyacanepaintingtier2; break;
            //case "kenyacanepatternvinyltier1.png" : returnThis = R.drawable.kenyacanepatternvinyltier1; break;
            //case "kenyacanepatternvinyltier2.png" : returnThis = R.drawable.kenyacanepatternvinyltier2; break;
            //case "kenyacanewallgraphicprintedtier1.png" : returnThis = R.drawable.kenyacanewallgraphicprintedtier1; break;
            //case "kenyacanewallgraphicprintedtier2.png" : returnThis = R.drawable.kenyacanewallgraphicprintedtier2; break;
            case "light_box_kenya_cane.png" : returnThis = R.drawable.light_box_kenya_cane; break;
            case "lounge_set_black_johnnie_walker.png" : returnThis = R.drawable.lounge_set_black_johnnie_walker; break;
            case "lounge_set_white_johnnie_walker.png" : returnThis = R.drawable.lounge_set_white_johnnie_walker; break;
            case "mobile_bar_johnnie_walker.png" : returnThis = R.drawable.mobile_bar_johnnie_walker; break;
            case "modrail_smirnoff.png" : returnThis = R.drawable.modrail_smirnoff; break;
            case "modular_back_of_bar_essentials_guinness.png" : returnThis = R.drawable.modular_back_of_bar_essentials_guinness; break;
            case "modular_back_of_bar_essentials_smirnoff.jpg" : returnThis = R.drawable.modular_back_of_bar_essentials_smirnoff; break;
            case "modular_back_of_bar_essentials_smirnoff.png" : returnThis = R.drawable.modular_back_of_bar_essentials_smirnoff; break;
            case "modular_back_of_bar_tier_1_guinness.png" : returnThis = R.drawable.modular_back_of_bar_tier_1_guinness; break;
            case "modular_back_of_bar_tier_1_smirnoff.png" : returnThis = R.drawable.modular_back_of_bar_tier_1_smirnoff; break;
            case "modular_back_of_bar_tier_2_guinness.png" : returnThis = R.drawable.modular_back_of_bar_tier_2_guinness; break;
            case "modular_back_of_bar_tier_2_smirnoff.png" : returnThis = R.drawable.modular_back_of_bar_tier_2_smirnoff; break;
            case "modular_back_of_bar_unit_header_lightbox_kenya_cane.png" : returnThis = R.drawable.modular_back_of_bar_unit_header_lightbox_kenya_cane; break;
            case "modular_illuminated_wall_panel_essentials_tusker.png" : returnThis = R.drawable.modular_illuminated_wall_panel_essentials_tusker; break;
            case "modular_illuminated_wall_panel_tier_1_tusker.png" : returnThis = R.drawable.modular_illuminated_wall_panel_tier_1_tusker; break;
            case "modular_illuminated_wall_panel_tier_2_tusker.png" : returnThis = R.drawable.modular_illuminated_wall_panel_tier_2_tusker; break;
            case "ottoman_johnnie_walker.png" : returnThis = R.drawable.ottoman_johnnie_walker; break;
            case "outdoor_coffee_table_johnnie_walker.png" : returnThis = R.drawable.outdoor_coffee_table_johnnie_walker; break;
            case "outlet_name_kenya_cane.png" : returnThis = R.drawable.outlet_name_kenya_cane; break;
            case "outlet_sign_smirnoff.png" : returnThis = R.drawable.outlet_sign_smirnoff; break;
            case "outlet_sign_tusker.png" : returnThis = R.drawable.outlet_sign_tusker; break;
            case "outside_signage_guinness.png" : returnThis = R.drawable.outside_signage_guinness; break;
            case "perfect_serve_kenya_cane.png" : returnThis = R.drawable.perfect_serve_kenya_cane; break;
//            case "smirnoffbarfrontvinyl_logotier1.png" : returnThis = R.drawable.smirnoffbarfrontvinyl_logotier1; break;
//            case "smirnoffbarfrontvinyl_logotier2.png" : returnThis = R.drawable.smirnoffbarfrontvinyl_logotier2; break;
//            case "smirnoffbarfrontvinyl_nologotier1.png" : returnThis = R.drawable.smirnoffbarfrontvinyl_nologotier1; break;
//            case "smirnoffbarfrontvinyl_nologotier2.png" : returnThis = R.drawable.smirnoffbarfrontvinyl_nologotier2; break;
//            case "smirnoffpaintingessentials.png" : returnThis = R.drawable.smirnoffpaintingessentials; break;
//            case "smirnoffpaintingtier1tier1.png" : returnThis = R.drawable.smirnoffpaintingtier1tier1; break;
//            case "smirnoffpaintingtier2.png" : returnThis = R.drawable.smirnoffpaintingtier2; break;
//            case "smirnoffsmirnoffpatternvinyltier1.png" : returnThis = R.drawable.smirnoffsmirnoffpatternvinyltier1; break;
//            case "smirnoffsmirnoffpatternvinyltier2.png" : returnThis = R.drawable.smirnoffsmirnoffpatternvinyltier2; break;
//            case "smirnoffwallgraphicprintedtier1.png" : returnThis = R.drawable.smirnoffwallgraphicprintedtier1; break;
//            case "smirnoffwallgraphicprintedtier2.png" : returnThis = R.drawable.smirnoffwallgraphicprintedtier2; break;
            case "snapframe_smirnoff.png" : returnThis = R.drawable.snapframe_smirnoff; break;
            case "solar_light_box_guinness.png" : returnThis = R.drawable.solar_light_box_guinness; break;
            case "solar_snap_frame_johnnie_walker.png" : returnThis = R.drawable.solar_snap_frame_johnnie_walker; break;
            case "table_defender_kenya_cane.png" : returnThis = R.drawable.table_defender_kenya_cane; break;
            case "table_defender_tusker.png" : returnThis = R.drawable.table_defender_tusker; break;
            case "table_talker_guinness.png" : returnThis = R.drawable.table_talker_guinness; break;
//            case "tuskerbarfrontvinyl_logotier1.png" : returnThis = R.drawable.tuskerbarfrontvinyl_logotier1; break;
//            case "tuskerbarfrontvinyl_nologotier1.png" : returnThis = R.drawable.tuskerbarfrontvinyl_nologotier1; break;
//            case "tuskerpaintingessentials.png" : returnThis = R.drawable.tuskerpaintingessentials; break;
//            case "tuskerpaintingtier1.png" : returnThis = R.drawable.tuskerpaintingtier1; break;
//            case "tuskerpaintingtier2.png" : returnThis = R.drawable.tuskerpaintingtier2; break;
//            case "tuskerpatternvinyltier1.png" : returnThis = R.drawable.tuskerpatternvinyltier1; break;
//            case "tuskerpatternvinyltier2.png" : returnThis = R.drawable.tuskerpatternvinyltier2; break;
//            case "tuskerwallgraphicprintedtier1.png" : returnThis = R.drawable.tuskerwallgraphicprintedtier1; break;
//            case "tuskerwallgraphicprintedtier2.png" : returnThis = R.drawable.tuskerwallgraphicprintedtier2; break;
            case "tv_cladding_guinness.png" : returnThis = R.drawable.tv_cladding_guinness; break;
            case "tv_cladding_kenya_cane.png" : returnThis = R.drawable.tv_cladding_kenya_cane; break;
            case "tv_wall_essential_tusker.png" : returnThis = R.drawable.tv_wall_essential_tusker; break;
            case "tv_wall_premium_tusker.png" : returnThis = R.drawable.tv_wall_premium_tusker; break;
            case "uhi_board_guinness.png" : returnThis = R.drawable.uhi_board_guinness; break;
            case "uhi_board_kenya_cane.png" : returnThis = R.drawable.uhi_board_kenya_cane; break;
            case "wall_branding_essentials_guinness.png" : returnThis = R.drawable.wall_branding_essentials_guinness; break;
            case "wall_branding_graphic_panels_essentials_guinness.png" : returnThis = R.drawable.wall_branding_graphic_panels_essentials_guinness; break;
            case "wall_branding_graphic_panels_johnnie_walker.png" : returnThis = R.drawable.wall_branding_graphic_panels_johnnie_walker; break;
            case "wall_branding_graphic_panels_kenya_cane.png" : returnThis = R.drawable.wall_branding_graphic_panels_kenya_cane; break;
            case "wall_branding_graphic_panels_smirnoff.jpg" : returnThis = R.drawable.wall_branding_graphic_panels_smirnoff; break;
            case "wall_branding_graphic_panels_smirnoff.png" : returnThis = R.drawable.wall_branding_graphic_panels_smirnoff; break;
            case "wall_branding_graphic_panels_tier_1_guinness.png" : returnThis = R.drawable.wall_branding_graphic_panels_tier_1_guinness; break;
            case "wall_branding_graphic_panels_tier_2_guinness.png" : returnThis = R.drawable.wall_branding_graphic_panels_tier_2_guinness; break;
            case "wall_branding_graphic_panels_tusker.png" : returnThis = R.drawable.wall_branding_graphic_panels_tusker; break;
            case "wall_branding_tier_1_guinness.png" : returnThis = R.drawable.wall_branding_tier_1_guinness; break;
            case "wall_branding_tier_2_guinness.png" : returnThis = R.drawable.wall_branding_tier_2_guinness; break;
            case "wall_counter_kenya_cane.png" : returnThis = R.drawable.wall_counter_kenya_cane; break;
            case "wall_mirrors_kenya_cane.png" : returnThis = R.drawable.wall_mirrors_kenya_cane; break;
            case "wall_mirrors_smirnoff.png" : returnThis = R.drawable.wall_mirrors_smirnoff; break;
            case "wall_mounted_sign_smirnoff.png" : returnThis = R.drawable.wall_mounted_sign_smirnoff; break;
            default: returnThis = R.drawable.accessory_placeholder; break;
        }

        return returnThis;
    }

}
