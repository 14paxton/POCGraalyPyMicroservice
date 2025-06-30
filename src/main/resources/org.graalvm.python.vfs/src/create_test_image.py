from PIL import Image, ImageDraw


def create_test_image(output_path='test_image.png'):
    # Create a new image with a white background
    img = Image.new('RGB', (800, 200), color='white')
    draw = ImageDraw.Draw(img)

    # Add text to the image
    text = "Hello! This is a test image for OCR.\nIt should convert this text correctly."
    draw.text((50, 50), text, fill='black', font=None, size=36)

    # Save the image
    img.save(output_path)
