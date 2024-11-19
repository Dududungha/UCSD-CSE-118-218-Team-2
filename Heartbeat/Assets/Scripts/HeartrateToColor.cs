using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HeartrateToColor : MonoBehaviour
{
    public Material SkyboxMaterial;

    [Range(50f, 200f)]
    public float Heartrate; 

    // we want the gradient from warm blue (hue: 255) to red (hue: 0)
    private float hue = 255f/360f;
    private float saturation = 0.4f;
    private float brightness = 1f;
    // private float time;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        hue = (235f + ((0f - 235f) / (200f - 50f)) * (Heartrate - 50f)) / 360f;
        SkyboxMaterial.SetColor("_Tint", Color.HSVToRGB(hue, saturation, brightness));
    }
}
