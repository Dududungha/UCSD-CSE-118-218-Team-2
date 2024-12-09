using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HeartrateToColor : MonoBehaviour
{
    public Material SkyboxMaterial;

    // we want the gradient from warm blue (hue: 255) to red (hue: 0)
    private float hue = 255f/360f;
    private float saturation = 0.4f;
    private float brightness = 1f;
    // private float time;

    // Start is called before the first frame update
    void Start()
    {
        HeartrateEventManager.OnHeartrateUpdate += SetSkyboxColor;
    }

    private void SetSkyboxColor(object sender, HeartrateEventArgs e) {
        hue = (235f + ((0f - 235f) / (150f - 50f)) * (e.heartrate - 50f)) / 360f;
        SkyboxMaterial.SetColor("_Tint", Color.HSVToRGB(hue, saturation, brightness));
    }
}
